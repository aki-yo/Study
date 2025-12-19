package com.oh.tp;

import java.util.*;

public class Study {

    public static void main(String[] args) {
        System.out.println("hello world");
        List<Integer> list = List.of(1, 2, 3, 4, 5);
//        list.
        System.out.println(list);
        List<Integer> list1 = new ArrayList<>(List.of(1, 2, 32, 4, 5));
        System.out.println(list1);
        list1.addAll(List.of(1, 2, 3, 4, 5));
        list1.add(99);
        list1.remove(0);
        list1.remove(Integer.valueOf(99));
        System.out.println(list1);
        list.get(0);
//        list.set(0, 99);// 可以看到用list和arrayList的set方法是不同的
        list1.set(0, 99);
        list1.clear();list1.size();list1.isEmpty();
        // add remove set get isEmpty size clear
        list.size();
        int arr[] = new int[10];
        int []arr1 = {1, 2, 3, 4, 5};
        System.out.println(arr1.length);
        System.out.println(arr.length);

        Map<String,String> map = new HashMap<>();
        map.put("1", "1");
        map.remove("1");
        System.out.println(map.get("1"));
        System.out.println(map);
        map.put("1", "2");
        map.put("1", "3");
        map.isEmpty();map.clear();map.size();
        System.out.println(map);
        System.out.println(map.get("1"));
        // put remove put get clear isEmpty size
        Set<String> set = new HashSet<>();
        set.add("1");
        set.remove("1");
        set.clear();set.isEmpty();set.size();
        // add remove clear isEmpty size
        Queue<String> queue = new LinkedList<>();
        queue.add("1");
        queue.remove("1");
        queue.clear();
        queue.isEmpty();
        queue.size();
        queue.poll();
        queue.offer("1");
        queue.peek();
        queue.remove();
        queue.element();

    }
}
