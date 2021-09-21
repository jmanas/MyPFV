package mypfv;

import java.util.*;

public class Simulator {
    // min/hour, hour/day, day/week
    private static final int N = 4;

    // max size of queues
    private static final int Q_LIMIT = 3;

    // min, hour, day, week queues
    private static final List<Item1> mList = new ArrayList<>();
    private static final List<Item1> hList = new ArrayList<>();
    private static final List<Item1> dList = new ArrayList<>();
    private static final List<Item1> wList = new ArrayList<>();

    public static void main(String[] args) {
        doit();
        System.out.println("min list:  " + mList);
        System.out.println("hour list: " + hList);
        System.out.println("day list:  " + dList);
        System.out.println("week list: " + wList);
    }

    private static void doit() {
        for (int week = 0; week < 4; week++) {
            for (int day = 0; day < 7; day++) {
                for (int hour = 0; hour < 24; hour++) {
                    for (int min = 0; min < 60; min++) {
                        store(new Item1(week, day, hour, min));
                    }
                }
            }
        }
    }

    private static void store(Item1 it) {
        System.out.println(it);
        push(it);
        purge();
    }

    private static void push(Item1 it) {
        Item1 m_last = getLast(mList);
        Item1 h_last = getLast(hList);
        Item1 d_last = getLast(dList);
        Item1 w_last = getLast(wList);

        if (m_last == null || getM(m_last, it) > 0)
            mList.add(it);
        if (m_last != null)
            if (h_last == null || getH(h_last, m_last) > 0)
                hList.add(m_last);
        if (h_last != null)
            if (d_last == null || getD(d_last, h_last) > 0)
                dList.add(h_last);
        if (d_last != null)
            if (w_last == null || getW(w_last, d_last) > 0)
                wList.add(d_last);
    }

    private static void purge() {
        Set<Item1> before = new HashSet<>();
        before.addAll(mList);
        before.addAll(hList);
        before.addAll(dList);
        before.addAll(wList);

        while (mList.size() > Q_LIMIT)
            mList.remove(0);
        while (hList.size() > Q_LIMIT)
            hList.remove(0);
        while (dList.size() > Q_LIMIT)
            dList.remove(0);
        while (wList.size() > Q_LIMIT)
            wList.remove(0);

        Set<Item1> after = new HashSet<>();
        after.addAll(mList);
        after.addAll(hList);
        after.addAll(dList);
        after.addAll(wList);

        Set<Item1> tbr = new HashSet<>(before);
        tbr.removeAll(after);

        System.out.println("TBR: " + tbr);
    }

    private static Item1 getLast(List<Item1> list) {
        if (list.isEmpty())
            return null;
        return list.get(list.size() - 1);
    }

    private static int getM(Item1 i1, Item1 i2) {
        if (i1 == null || i2 == null)
            return 1;
        return getH(i1, i2) * 60 + i2.m - i1.m;
    }

    private static int getH(Item1 i1, Item1 i2) {
        if (i1 == null || i2 == null)
            return 1;
        return getD(i1, i2) * 24 + i2.h - i1.h;
    }

    private static int getD(Item1 i1, Item1 i2) {
        if (i1 == null || i2 == null)
            return 1;
        return getW(i1, i2) * 7 + i2.d - i1.d;
    }

    private static int getW(Item1 i1, Item1 i2) {
        if (i1 == null || i2 == null)
            return 1;
        return i2.w - i1.w;
    }

    private static class Item1 {
        final int w, d, h, m;

        private Item1(int w, int d, int h, int m) {
            this.w = w;
            this.d = d;
            this.h = h;
            this.m = m;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Item1 item1 = (Item1) o;
            return w == item1.w && d == item1.d && h == item1.h && m == item1.m;
        }

        @Override
        public int hashCode() {
            return Objects.hash(w, d, h, m);
        }

        public String toString() {
            return String.format("<%d %d %d %d>", w, d, h, m);
        }
    }
}
