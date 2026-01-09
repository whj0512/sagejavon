package com.springboot.cli;

public class Test {
    public int maxValue(int[] nums){
        int max = Integer.MIN_VALUE;
        int left = 0, right = nums.length - 1;
        while(left<right){
            int area = (right-left)*(Math.min(nums[left], nums[right]));
            if(area>max){
                max = area;
            }
            if(nums[left]<nums[right]){
                right--;
            }else{
                left++;
            }
        }
        return max;
    }
    public static void main(String[] args) {
        Test test = new Test();
        int[] nums = {1,8,6,2,5,4,8,3,7};
        System.out.println(test.maxValue(nums));

    }
}
