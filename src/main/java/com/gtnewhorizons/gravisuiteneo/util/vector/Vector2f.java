package com.gtnewhorizons.gravisuiteneo.util.vector;

public class Vector2f {

    public float x;
    public float y;

    public Vector2f() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2f(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2d other) {
        this(other.x, other.y);
    }

    public Vector2f(Vector2f other) {
        this(other.x, other.y);
    }

    public void set(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public void add(Vector2f vec) {
        this.x += vec.x;
        this.y += vec.y;
    }

    public void sub(Vector2f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public void scale(double s) {
        this.x *= s;
        this.y *= s;
    }

    public void normalize() {
        double scale = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
        this.scale(scale);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double distanceSquared(Vector2f v) {
        double dx, dy;
        dx = this.x - v.x;
        dy = this.y - v.y;
        return dx * dx + dy * dy;
    }

    public double distance(Vector2f v) {
        return Math.sqrt(this.distanceSquared(v));
    }

    @Override
    public String toString() {
        return "Vector2f(" + this.x + ", " + this.y + ")";
    }
}
