import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileCopy {
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("使用法: java FileCopy <ソースパス> <出力先パス>");
            System.exit(1);
        }
        
        Path source = Paths.get(args[0]);
        Path destination = Paths.get(args[1]);
        
        try {
            copyPath(source, destination);
            System.out.println("コピーが完了しました。");
        } catch (IOException e) {
            System.err.println("コピー中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void copyPath(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("ソースパスが存在しません: " + source);
        }
        
        if (Files.isDirectory(source)) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }
    
    private static void copyFile(Path source, Path destination) throws IOException {
        if (Files.isDirectory(destination)) {
            destination = destination.resolve(source.getFileName());
        }
        
        Files.createDirectories(destination.getParent());
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("コピー: " + source + " -> " + destination);
    }
    
    private static void copyDirectory(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = destination.resolve(source.relativize(dir));
                try {
                    Files.createDirectories(targetDir);
                    System.out.println("ディレクトリ作成: " + targetDir);
                } catch (IOException e) {
                    if (!Files.isDirectory(targetDir)) {
                        throw e;
                    }
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = destination.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("コピー: " + file + " -> " + targetFile);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
