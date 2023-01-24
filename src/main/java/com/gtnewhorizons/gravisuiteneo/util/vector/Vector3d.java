package com.gtnewhorizons.gravisuiteneo.util.vector;

import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.Vertex;

public class Vector3d {

    public double x;
    public double y;
    public double z;

    public Vector3d() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Vector3d other) {
        this(other.x, other.y, other.z);
    }

    public Vector3d(Vector3f corner) {
        this(corner.x, corner.y, corner.z);
    }

    public Vector3d(Vertex vert) {
        this(vert.x, vert.y, vert.z);
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3f v) {
        this.set(v.x, v.y, v.z);
    }

    public void set(Vector3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3d add(Vector3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vector3d add(Vector3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vector3d add(double x2, double y2, double z2) {
        this.x += x2;
        this.y += y2;
        this.z += z2;
        return this;
    }

    public void sub(Vector3d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public void sub(Vector3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public Vector3d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public void scale(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public void scale(double sx, double sy, double sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
    }

    public void normalize() {
        double scale = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.scale(scale);
    }

    public double dot(Vector3d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public void cross(Vector3d v1, Vector3d v2) {
        this.x = v1.y * v2.z - v1.z * v2.y;
        this.y = v2.x * v1.z - v2.z * v1.x;
        this.z = v1.x * v2.y - v1.y * v2.x;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double distanceSquared(Vector3d v) {
        double dx, dy, dz;
        dx = this.x - v.x;
        dy = this.y - v.y;
        dz = this.z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distance(Vector3d v) {
        return Math.sqrt(this.distanceSquared(v));
    }

    @Override
    public String toString() {
        return "Vector3d(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public void abs() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }

    public Vec3 getVec3() {
        return Vec3.createVectorHelper(this.x, this.y, this.z);
    }
}
