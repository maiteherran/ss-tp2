package ss.g7q12020;

import java.util.ArrayList;
import java.util.List;

public class MatrixCell {

    List<Particle> particles;

    public MatrixCell(List<Particle> particles) {
        this.particles = particles;
    }

    public MatrixCell() {
        particles = new ArrayList<>();
    }

    public void addParticle(Particle p){
        particles.add(p);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }
}
