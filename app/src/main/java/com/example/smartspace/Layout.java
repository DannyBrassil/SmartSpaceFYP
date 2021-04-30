package com.example.smartspace;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Layout {

    private int columns, rows, height, width, floor;
    private ArrayList<tile> tiles;
    private ArrayList<Space> spaces;

    public Layout(int columns, int rows, int height, int width, ArrayList<tile> tiles, ArrayList<Space> spaces, int floor) {
        this.columns = columns;
        this.rows = rows;
        this.height = height;
        this.width = width;
        this.tiles = tiles;
        this.spaces=spaces;
        this.floor=floor;
    }

    public Layout(){

    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<tile> tiles) {
        this.tiles = tiles;
    }

    public ArrayList<Space> getSpaces() {
        return spaces;
    }

    public void setSpaces(ArrayList<Space> spaces) {
        this.spaces = spaces;
    }
}
