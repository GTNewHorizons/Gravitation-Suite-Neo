package com.gtnewhorizons.gravisuiteneo.util.vector;

public class Vector3f {

    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3f(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3d other) {
        this(other.x, other.y, other.z);
    }

    public Vector3f(Vector3f other) {
        this(other.x, other.y, other.z);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void add(Vector3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void add(Vector3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void add(int x2, int y2, int z2) {
        this.x += x2;
        this.y += y2;
        this.z += z2;
    }

    public void sub(Vector3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public void sub(Vector3d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public void scale(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public void normalize() {
        double scale = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public double dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    @Override
    public String toString() {
        return "Vector3f(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vector3d asVector3d() {
        return new Vector3d(this.x, this.y, this.z);
    }
}
