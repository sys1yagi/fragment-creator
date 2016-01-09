package com.sys1yagi.fragmentcreator.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Combinations<T> implements Iterator {

    private List<List<T>> combinations;

    private List<T> list;

    private int[] index;

    private boolean[] visited;

    private int r;

    private boolean overHalf;

    private Iterator<List<T>> iterator;

    public Combinations(T[] array, int r) throws IllegalArgumentException {
        if (array.length < 1 || r < 1 || array.length < r) {
            throw new IllegalArgumentException();
        }

        this.combinations = new ArrayList<>();
        this.list = Arrays.asList(array);
        this.r = r;
        if (this.r == array.length) {
            this.combinations.add(list);
        } else {
            if (this.r > list.size() / 2) {
                this.r = list.size() - this.r;
                this.overHalf = true;
            }

            this.index = new int[this.r];
            this.visited = new boolean[list.size()];
            this.compute(0);
        }

        this.iterator = this.combinations.iterator();
    }

    private void compute(int n) {
        if (n == this.r) {
            List<T> combination = new ArrayList<T>();
            if (overHalf) {
                for (int i = 0; i < this.list.size(); i++) {
                    boolean skip = false;
                    for (int j = 0; j < this.index.length; j++) {
                        if (i == this.index[j]) {
                            skip = true;
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    combination.add(list.get(i));
                }
            } else {
                for (int i = 0; i < this.index.length; i++) {
                    combination.add(list.get(index[i]));
                }
            }
            this.combinations.add(combination);
        } else {
            for (int i = 0; i < this.list.size(); i++) {
                if (n == 0 || !this.visited[i] && index[n - 1] < i) {
                    this.visited[i] = true;
                    this.index[n] = i;
                    this.compute(n + 1);
                    this.visited[i] = false;
                }
            }
        }
    }

    @Override
    public List<T> next() {
        return this.iterator.next();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
}
