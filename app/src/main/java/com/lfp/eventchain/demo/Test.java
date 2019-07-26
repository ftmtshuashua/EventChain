package com.lfp.eventchain.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <pre>
 * Tip:
 *
 * Function:
 *
 * Created by LiFuPing on 2019/7/17 16:53
 * </pre>
 */
public class Test {

    private static final class TiMu {
        int[] data;
        int value;

        public TiMu(int[] data, int value) {
            this.data = data;
            this.value = value;
        }

        @Override
        public String toString() {
            return "{" +
                    "data=" + Arrays.toString(data) +
                    ", value=" + value +
                    '}';
        }
    }

    /*----------------------- 求两数和 ---------------------*/
    public static void main2(String[] args) {

        ArrayList<TiMu> array = new ArrayList<>();
//        array.add(new TiMu(new int[]{2, 7, 11, 15}, 9));
//        array.add(new TiMu(new int[]{-3, 4, 3, 90}, 0));
//        array.add(new TiMu(new int[]{3, 3}, 6));

        int lenth = 2 + 10000000;
        int[] data = new int[lenth];
        for (int i = 1; i < lenth - 1; i++) {
            data[i] = 10;
        }
        data[0] = 0;
        data[lenth - 1] = 6;
        array.add(new TiMu(data, 6));


        long time = System.currentTimeMillis();
        for (TiMu item : array) {
            final int[] ints = new Test().twoSum(item.data, item.value);
//            final int[] ints = new Test().twoSum3(item.data, item.value);

            System.err.println("-------------输出-------------");
//            System.err.println("题目:" + item);
            System.err.println("结果:" + (ints == null ? "null" : (ints[0] + " , " + ints[1])));
        }
        System.err.println("耗时：" + (System.currentTimeMillis() - time) + " ms");
    }

    /* (n)2 */
    public int[] twoSum2(int[] nums, int target) {
        int length = nums.length;
        for (int i = 0; i < length; i++) {
            int number = nums[i];
            for (int j = i + 1; j < length; j++) {
                int number_2 = nums[j];

                if (number + number_2 == target) return new int[]{i, j};
            }
        }
        return null;
    }


    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> array = new HashMap<>();
        int number;
        int cz;


        for (int i = 0, size = nums.length; i < size; i++) {
            number = nums[i];

            cz = target - number;
            if (array.get(cz) != null) {
                return new int[]{array.get(cz), i};
            } else {
                array.put(number, i);
            }
        }

        return null;
    }

    public int[] twoSum3(int[] nums, int target) {
        int index;
        int indexArrayMax = 2047;
        int[] indexArrays = new int[indexArrayMax + 1];
        int diff;
        for (int i = 1; i < nums.length; i++) {
            diff = target - nums[i];
            if (diff == nums[0]) {
                return new int[]{0, i};
            }
            index = diff & indexArrayMax;
            if (indexArrays[index] != 0) {
                return new int[]{indexArrays[index], i};
            }
            indexArrays[nums[i] & indexArrayMax] = i;
        }
        return new int[2];
    }

    /*----------------------- 链表加法 ---------------------*/

    public static void main3(String[] args) {

    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) { val = x; }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode root = null;
        ListNode next = null;

        int v1, v2, add = 0;
        int he, _he;

        while (l1 != null || l2 != null || add > 0) {
            v1 = l1 == null ? 0 : l1.val;
            v2 = l2 == null ? 0 : l2.val;

            he = v1 + v2 + add;
            _he = he >= 10 ? he % 10 : he;


            if (root == null) {
                root = new ListNode(_he);
            } else {

                if (next == null) {
                    next = new ListNode(_he);
                    root.next = next;
                } else {
                    next.next = new ListNode(_he);
                    next = next.next;
                }
            }

            add = he >= 10 ? he / 10 : 0;
            l1 = l1 == null ? null : l1.next;
            l2 = l2 == null ? null : l2.next;
        }


        return root;
    }

    /*----------------------- 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。 ---------------------*/

    public static void main(String[] args) {
        System.err.println("个数:"+new Test().lengthOfLongestSubstring("abcabcbb"));
    }

    public int lengthOfLongestSubstring(String s) {

        int max = 0;
        int count = 0;
        int[] array = new int[128];
        for (int j = 0; j < array.length; j++) {
            array[j] = -1;
        }
        for (int i = 0; i < s.length(); i++) {
            final int index = s.charAt(i);

            if (array[index] == -1) {
                array[index] = i;
                count++;
            } else {
                max = (max >= count) ? max : count;
                count = 0;
                i=array[index];
                for (int j = 0; j < array.length; j++) {
                    array[j] = -1;
                }
            }
        }
        max = (max >= count) ? max : count;

        return max;
    }
}

