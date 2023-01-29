package com.gtnewhorizons.gravisuiteneo.util.vector;

public class Vector4f {

    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(double x, double y, double z, double w) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.w = (float) w;
    }

    public Vector4f(Vector4f other) {
        this(other.x, other.y, other.z, other.w);
    }

    public void interpolate(Vector4f destination, float factor) {
        this.x = (1 - factor) * this.x + factor * destination.x;
        this.y = (1 - factor) * this.y + factor * destination.y;
        this.z = (1 - factor) * this.z + factor * destination.z;
        this.w = (1 - factor) * this.w + factor * destination.w;
    }

    public void set(Vector4f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void add(Vector4f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
    }

    public void sub(Vector4f vec) {
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

    public double dot(Vector4f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public Vector3f toVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return "Vector4f(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }
}
