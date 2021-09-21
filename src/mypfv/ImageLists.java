package mypfv;

import java.io.File;
import java.util.*;

public class ImageLists {
    private final Image1 now;
    private final List<Image1> minList = new ArrayList<>();
    private final List<Image1> hourList = new ArrayList<>();
    private final List<Image1> dayList = new ArrayList<>();
    private final List<Image1> weekList = new ArrayList<>();

    static ImageLists mk(File to, File srcFile) {
        ImageLists kit = new ImageLists();
        File[] files = to.listFiles();
        if (files == null || files.length == 0)
            return kit;
        for (File img : files) {
            Image1 image = Image1.mkFromStore(srcFile, img);
            if (image != null)
                kit.addStore(image);
        }
        kit.sort();
        return kit;
    }

    ImageLists() {
        now = Image1.mkFakeNow();
    }

    private void addStore(Image1 image) {
        if (Utils.getWeeks(image, now) > 7)
            weekList.add(image);
        else if (Utils.getDays(image, now) > 0)
            dayList.add(image);
        else if (Utils.getHours(image, now) > 0)
            hourList.add(image);
        else
            minList.add(image);
    }

    private void sort() {
        minList.sort(Comparator.comparing(Image1::getDatePrefix));
        hourList.sort(Comparator.comparing(Image1::getDatePrefix));
        dayList.sort(Comparator.comparing(Image1::getDatePrefix));
        weekList.sort(Comparator.comparing(Image1::getDatePrefix));
    }

    public Image1 last() {
        if (minList.size() > 0)
            return minList.get(minList.size() - 1);
        if (hourList.size() > 0)
            return hourList.get(hourList.size() - 1);
        if (dayList.size() > 0)
            return dayList.get(dayList.size() - 1);
        if (weekList.size() > 0)
            return weekList.get(weekList.size() - 1);
        return null;
    }

    void push(Image1 it) {
        Image1 m_last = getLast(minList);
        Image1 h_last = getLast(hourList);
        Image1 d_last = getLast(dayList);
        Image1 w_last = getLast(weekList);

        if (m_last == null || Utils.getMins(m_last, it) > 0)
            minList.add(it);
        if (m_last == null)
            return;
        if (h_last == null || Utils.getHours(h_last, m_last) > 0)
            hourList.add(m_last);
        if (d_last == null || Utils.getDays(d_last, m_last) > 0)
            dayList.add(m_last);
        if (w_last == null || Utils.getWeeks(w_last, m_last) > 0)
            weekList.add(m_last);
    }

    public void purge(int logLevel, int limitM, int limitH, int limitD, int limitW) {
        Set<Image1> before = new HashSet<>();
        before.addAll(minList);
        before.addAll(hourList);
        before.addAll(dayList);
        before.addAll(weekList);

        while (minList.size() > limitM)
            minList.remove(0);
        while (hourList.size() > limitH)
            hourList.remove(0);
        while (dayList.size() > limitD)
            dayList.remove(0);
        while (weekList.size() > limitW)
            weekList.remove(0);

        Set<Image1> after = new HashSet<>();
        after.addAll(minList);
        after.addAll(hourList);
        after.addAll(dayList);
        after.addAll(weekList);

        Set<Image1> tbr = new HashSet<>(before);
        tbr.removeAll(after);
        if (tbr.size() > 1)
            System.out.println("tbr.size(): " + tbr.size());

        for (Image1 img : tbr) {
            if (logLevel > 2)
                Logger.log("delete " + img);
            img.getImg().delete();
        }
    }

    private Image1 getLast(List<Image1> list) {
        if (list.isEmpty())
            return null;
        return list.get(list.size() - 1);
    }
}
