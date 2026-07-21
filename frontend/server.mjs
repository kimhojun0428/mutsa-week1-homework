import { createServer } from "node:http";
import { readFile, stat } from "node:fs/promises";
import { dirname, extname, join, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";

const host = "127.0.0.1";
const port = 5173;
const root = dirname(fileURLToPath(import.meta.url));
const contentTypes = {
  ".css": "text/css; charset=utf-8",
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
};

createServer(async (request, response) => {
  try {
    const url = new URL(request.url ?? "/", `http://${request.headers.host}`);
    const requestedPath = decodeURIComponent(url.pathname).replace(/^\/+/, "");
    let filePath = resolve(root, requestedPath || "index.html");

    if (filePath !== root && !filePath.startsWith(`${root}${sep}`)) {
      response.writeHead(403).end("Forbidden");
      return;
    }

    try {
      if ((await stat(filePath)).isDirectory()) {
        filePath = join(filePath, "index.html");
      }
    } catch {
      // OAuth2 성공/실패 경로도 같은 단일 화면에서 처리한다.
      filePath = join(root, "index.html");
    }

    const body = await readFile(filePath);
    response.writeHead(200, {
      "Content-Type": contentTypes[extname(filePath)] ?? "application/octet-stream",
      "Cache-Control": "no-store",
    });
    response.end(body);
  } catch (error) {
    response.writeHead(500, { "Content-Type": "text/plain; charset=utf-8" });
    response.end(`테스트 화면을 불러오지 못했습니다: ${error.message}`);
  }
}).listen(port, host, () => {
  console.log(`카카오 로그인 테스트 화면: http://localhost:${port}`);
});
