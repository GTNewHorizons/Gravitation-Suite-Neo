package com.gtnewhorizons.gravisuiteneo.util.vector;

public class Vector4d {

    public double x;
    public double y;
    public double z;
    public double w;

    public Vector4d() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vector4d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4d(Vector4d other) {
        this(other.x, other.y, other.z, other.w);
    }

    public void set(Vector4d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void add(Vector4d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
    }

    public void sub(Vector4d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        this.w -= vec.w;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public void scale(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.w *= s;
    }

    public void normalize() {
        double scale = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        this.scale(scale);
    }

    public double dot(Vector4d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    @Override
    public String toString() {
        return "Vector4d(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }
}
