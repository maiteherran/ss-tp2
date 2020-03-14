package ss.g7q12020;

import java.util.Objects;

public class Particle {

    long id;

    double radius;
    double x;
    double y;

    double vx;
    double vy;

//    public Particle(long id, double x, double y, double radius) {
//        this.id = id;
//        this.x = x;
//        this.y = y;
//        this.radius = radius;
//    }
//
//    //para el input dinamico
//    public Particle(long id, double x, double y) {
//        this.id = id;
//        this.x = x;
//        this.y = y;
//        this.radius = 0;
//    }
//    public Particle(long id, double x, double y, double vx, double vy, double radius) {
//        this.id = id;
//        this.x = x;
//        this.y = y;
//        this.vx = vx;
//        this.vy = vy;
//        this.radius = radius;
//
//    }

    public Particle(double x, double y, double vx, double vy, double radius) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
