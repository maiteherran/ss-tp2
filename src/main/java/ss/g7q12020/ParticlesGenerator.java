package ss.g7q12020;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticlesGenerator {
    private long N;
    private Double L;

    private final static Double MIN_RADIUS = 0.0;
    private final static Double MAX_RADIUS = 0.0;
    private final static Double MIN_ANGLE = 0.0;
    private final static Double MAX_ANGLE = 2*Math.PI;
//    private final static Double MIN_VEL = 0.03;
//    private final static Double MAX_VEL = 0.03;
    private final static double defaultSpeedModule = 0.03;

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public ParticlesGenerator(long N, Double L) {
        this.N = N;
        this.L = L;
    }

    public List<Particle> generate() {
        Random rand = new Random();
        List<Particle> initialParticlesDisposition = new ArrayList<>();
        while (initialParticlesDisposition.size() != N) {
//            Particle p = new Particle(
//                    atomicInteger.incrementAndGet()-1,
//                    rand.nextDouble() * L,
//                    rand.nextDouble() * L,
//                    rand.nextDouble() * (MAX_VEL - MIN_VEL) + MIN_VEL,
//                    rand.nextDouble() * (MAX_VEL - MIN_VEL) + MIN_VEL,
//                    rand.nextDouble() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS
//            );
            Particle p = new Particle(
                    atomicInteger.incrementAndGet()-1,
                    rand.nextDouble() * L,
                    rand.nextDouble() * L,
                    defaultSpeedModule,
                    rand.nextDouble() * (MAX_ANGLE - MIN_ANGLE) + MIN_ANGLE,
                    rand.nextDouble() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS
            );
            boolean dontOverlap = initialParticlesDisposition.stream().noneMatch(particle ->
                    Math.sqrt(Math.pow(particle.x - p.x, 2) + Math.pow(particle.y - p.y, 2)) < particle.radius + p.radius);
            if (dontOverlap) {
                initialParticlesDisposition.add(p);
            }
        }
        return initialParticlesDisposition;
    }

}
