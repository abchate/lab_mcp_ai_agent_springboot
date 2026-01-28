import express from "express";
import bodyParser from "body-parser";
import { StdioClientTransport } from "@modelcontextprotocol/sdk/client/stdio.js";
import { Client } from "@modelcontextprotocol/sdk/client/index.js";

const PORT = Number(process.env.PORT || 3333);
const PATH = process.env.MCP_PATH || "/mcp";
const TOKEN = process.env.GITHUB_PERSONAL_ACCESS_TOKEN;

if (!TOKEN) {
    console.error("Missing env var: GITHUB_PERSONAL_ACCESS_TOKEN");
    process.exit(1);
}

// Command to start the official GitHub MCP server (STDIO)
const COMMAND = process.env.GITHUB_MCP_CMD || "docker";
const ARGS = (process.env.GITHUB_MCP_ARGS ||
    `run --rm -i -e GITHUB_PERSONAL_ACCESS_TOKEN=${TOKEN} ghcr.io/github/github-mcp-server`
).split(" ");

const transport = new StdioClientTransport({
    command: COMMAND,
    args: ARGS,
    env: { ...process.env }, // keep env (not strictly required since token is in args)
});

const client = new Client(
    { name: "github-mcp-http-wrapper", version: "1.0.0" },
    { capabilities: {} }
);

await client.connect(transport);

const app = express();
app.use(bodyParser.json({ limit: "1mb" }));

app.post(PATH, async (req, res) => {
    const { jsonrpc, id, method, params } = req.body || {};
    try {
        if (jsonrpc !== "2.0") throw new Error("Invalid jsonrpc version");
        if (!id) throw new Error("Missing id");

        if (method === "tools/list") {
            const result = await client.listTools();
            return res.json({ jsonrpc: "2.0", id, result });
        }

        if (method === "tools/call") {
            const { name, arguments: args } = params || {};
            if (!name) throw new Error("Missing tool name");
            const result = await client.callTool({ name, arguments: args || {} });
            return res.json({ jsonrpc: "2.0", id, result });
        }

        return res.json({
            jsonrpc: "2.0",
            id,
            error: { code: -32601, message: "Method not found" },
        });
    } catch (e) {
        return res.json({
            jsonrpc: "2.0",
            id: id ?? "1",
            error: { code: -32000, message: String(e.message || e) },
        });
    }
});

app.get("/healthz", (_req, res) => res.status(200).send("ok"));

app.listen(PORT, () => {
    console.log(`GitHub MCP HTTP Wrapper listening on http://localhost:${PORT}${PATH}`);
});