package mypfv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Image1 {
    private static final String DATE_PATTERN = "yyyyMMddHHmm";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private final File img;
    private final String datePrefix;
    private final LocalDateTime date;

    public static Image1 mkFakeNow() {
        LocalDateTime now = LocalDateTime.now();
        String prefix = DATE_FORMATTER.format(now);
        return new Image1(prefix, null);
    }

    public static Image1 mkNew(File to, File srcFile)
            throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String prefix = DATE_FORMATTER.format(now);
        File newImg = new File(to, prefix + "-" + srcFile.getName());
        if (newImg.exists())
            return null;
        Files.copy(srcFile.toPath(), newImg.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
        return new Image1(prefix, newImg);
    }

    public static Image1 mkFromStore(File src, File img) {
        try {
            int prfx_length = DATE_PATTERN.length();
            String imgName = img.getName();
            if (imgName.charAt(prfx_length) != '-')
                return null;
            String datePrefix = imgName.substring(0, prfx_length);
            String filename = imgName.substring(prfx_length + 1);
            if (!src.getName().equals(filename))
                return null;
            return new Image1(datePrefix, img);
        } catch (Exception e) {
            return null;
        }
    }

    private Image1(String datePrefix, File img) {
        this.datePrefix = datePrefix;
        this.img = img;
        date = LocalDateTime.from(DATE_FORMATTER.parse(datePrefix));
    }

    public File getImg() {
        return img;
    }

    public String getDatePrefix() {
        return datePrefix;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return img.toString();
    }

    public static void main(String[] args)
            throws IOException {
        test1();
        test2();
    }

    private static void test1() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(DATE_FORMATTER.format(now));
    }

    private static void test2()
            throws IOException {
        Image1 image1 = Image1.mkFromStore(new File("C:/test.txt"), new File("C:/202106111200-test.txt"));
        System.out.println(image1.getDatePrefix());
        System.out.println(image1.getImg().getCanonicalFile());
    }
}
