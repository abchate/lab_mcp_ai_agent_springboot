FROM node:20-alpine

WORKDIR /app

# Install Docker CLI (client only)
RUN apk add --no-cache docker-cli

# Install wrapper deps
COPY package.json package-lock.json* ./
RUN npm install --omit=dev

# Copy wrapper source
COPY src ./src

EXPOSE 3333
ENV PORT=3333
ENV MCP_PATH=/mcp

CMD ["npm","start"]