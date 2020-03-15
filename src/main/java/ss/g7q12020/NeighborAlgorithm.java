package ss.g7q12020;

import java.util.*;


public class NeighborAlgorithm {

    public static Map<Long, Set<Long>> findNeighbors(List<Particle> particleList,
                                                     boolean circularBorder, double rc, double length) {

        long startTime = System.nanoTime();
        Map<Long, Set<Long>> idNeighborsMap = cimAlgorithm(particleList, circularBorder,rc, length);
        //Map<Long, Set<Long>> idNeighborsMap = bruteForceAlgorithm(particleList,circularBorder,rc,length);
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Tiempo de ejecución del calculo de vecinos: " + totalTime / 1000000 + " ms");

        return idNeighborsMap;
    }

    private static Map<Long, Set<Long>> bruteForceAlgorithm(List<Particle> particleList, boolean border, double rc, double length) {
        Map<Long, Set<Long>> idNeighborsMap = new HashMap<>();
        for (int i = 0; i < particleList.size(); i++) {
            Set<Long> idList = new HashSet<>();
            for (int j = 0; j < particleList.size(); j++) {
                double xVal = particleList.get(i).getX() - particleList.get(j).getX();
                double yVal = particleList.get(i).getY() - particleList.get(j).getY();
                if (border) {
                    xVal = Math.min(xVal,
                            (length - particleList.get(i).getX() - particleList.get(j).getX()));
                    yVal = Math.min(yVal,
                            (length - particleList.get(i).getY() - particleList.get(j).getY()));
                }
                if (i != j && rc >= Math.sqrt(Math.pow(xVal, 2) + Math.pow(yVal, 2))
                        - particleList.get(j).getRadius() - particleList.get(i).getRadius()) {
                    idList.add(particleList.get(j).getId());
                }
            }
            idNeighborsMap.put(particleList.get(i).getId(), idList);
        }
        return idNeighborsMap;
    }

    private static Map<Long, Set<Long>> cimAlgorithm(List<Particle> particleList, boolean circularBorder, double rc, double length) {
        double maxRadius = 0;

        for (Particle p : particleList) {
            maxRadius = Math.max(maxRadius, p.radius);
        }

        int m = (int) Math.floor(length / (rc + 2 * maxRadius));
        //int m = 2;
        System.out.println("m = " + m);

        MatrixCell[][] areaMatrix = new MatrixCell[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                areaMatrix[i][j] = new MatrixCell();
            }
        }

        for (Particle p : particleList) {
            double x = p.x;
            double y = p.y;
            int mx = 0;
            int my = 0;
            while (x >= 0) {
                x -= length / (double) m;
                mx++;
            }
            while (y >= 0) {
                y -= length / (double) m;
                my++;
            }
            areaMatrix[mx - 1][my - 1].addParticle(p);
        }

        Map<Long, Set<Long>> idNeighborsMap = new HashMap<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                for (Particle particle : areaMatrix[i][j].getParticles()) {
                    Set<Long> neighList = findNeighborParticles(particle, areaMatrix, i, j, circularBorder, rc, length);

                    Set<Long> oldSet = idNeighborsMap.get(particle.getId());
                    if (oldSet == null) {
                        idNeighborsMap.put(particle.getId(), neighList);
                    } else {
                        oldSet.addAll(neighList);
                        idNeighborsMap.remove(particle.getId());
                        idNeighborsMap.put(particle.getId(), oldSet);
                    }
                    for (Long neighborRight : neighList) {
                        Set<Long> auxSet = idNeighborsMap.get(neighborRight);
                        if (auxSet == null && particle.getId() != neighborRight) {
                            Set<Long> newSet = new HashSet<>();
                            newSet.add(particle.getId());
                            idNeighborsMap.put(neighborRight, newSet);
                        } else if (auxSet != null && particle.getId() != neighborRight) {
                            auxSet.add(particle.getId());
                            idNeighborsMap.remove(neighborRight);
                            idNeighborsMap.put(neighborRight, auxSet);
                        }

                    }
                }
            }
        }
        return idNeighborsMap;
    }

    private static Set<Long> findNeighborParticles(Particle particle, MatrixCell[][] areaMatrix, int x,
                                                   int y, boolean border, double rc, double length) {
        Set<Long> neighbors = new HashSet<>();

        for (Particle p : areaMatrix[x][y].getParticles()) {
            if (!p.equals(particle)) {
                double calculation = Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(particle.getY() - p.getY(), 2)) - p.getRadius() - particle.getRadius();
                if (rc >= calculation) {//veo si la dist es menor que rc
                    neighbors.add(p.getId());
                }
            }
        }

        if (y >= areaMatrix.length - 1) {
            if (border) {
                for (Particle p : areaMatrix[x][0].getParticles()) {
                    double yPos = (length - particle.getY()) - p.getY();
                    if (rc >= Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(yPos, 2)) - p.getRadius() - particle.getRadius()) {//veo si la dist es menor que rc
                        neighbors.add(p.getId());
                    }
                }
            }
        } else {
            for (Particle p : areaMatrix[x][y + 1].getParticles()) {
                if (rc >= Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(particle.getY() - p.getY(), 2)) - p.getRadius() - particle.getRadius()) {//veo si la dist es menor que rc
                    neighbors.add(p.getId());
                }
            }
        }

        for (int i = -1; i <= 1; i++) {
            int aux = y + i > 0 ? y : areaMatrix.length - 1;
            aux = aux + i < areaMatrix.length ? aux : 0;
            if (x >= areaMatrix.length - 1) {
                if (border) {
                    for (Particle p : areaMatrix[0][aux + i].getParticles()) {
                        double xPos = (length - particle.getX()) - (p.getX());
                        double yPos = particle.getY() - p.getY();
                        if (aux != y) {
                            yPos = (length - particle.getY()) - p.getY();
                        }

                        double calculation = Math.sqrt(Math.pow(xPos, 2) + Math.pow(yPos, 2)) - p.getRadius() - particle.getRadius();
                        if (rc >= calculation) {//veo si la dist es menor que rc
                            neighbors.add(p.getId());
                        }
                    }
                }
            } else {
                if (border || aux == y) {
                    for (Particle p : areaMatrix[x + 1][aux + i].getParticles()) {
                        double yPos = particle.getY() - p.getY();
                        double calculation = Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(yPos, 2)) - p.getRadius() - particle.getRadius();
                        if (rc >= calculation) {//veo si la dist es menor que rc
                            neighbors.add(p.getId());
                        }
                    }
                }
            }
        }
        return neighbors;
    }

//    private static List<Particle> getParticleData(String staticFile, String dynamicFile) throws Exception {
//        BufferedReader staticBuffer = new BufferedReader(new FileReader(new File(staticFile)));
//        String str = staticBuffer.readLine();
//        if (str == null) {
//            throw new Exception();
//        }
//
//        N = Long.parseLong(str);
//        str = staticBuffer.readLine();
//        if (str != null) {
//            length = Double.parseDouble(str);
//        } else {
//            throw new Exception();
//        }
//
//        List<Double> radiusList = new ArrayList<>();
//        while ((str = staticBuffer.readLine()) != null) {
//            String[] listStatic = str.split(" ");
//            radiusList.add(Double.parseDouble(listStatic[0]));
//        }
//
//        List<Particle> particleList = new ArrayList<>();
//        BufferedReader dynamicBuffer = new BufferedReader(new FileReader(new File(dynamicFile)));
//        str = dynamicBuffer.readLine();
//        if (str == null) {//por ahora salteo t0
//            throw new Exception();
//        }
//
//        String[] dynamicFileList;
//        int i = 0;
//        while ((str = dynamicBuffer.readLine()) != null && (dynamicFileList = str.split(" ")).length > 1) {
//            //la segunda condicion es para agarrar apenas t0
//            particleList.add(
//                    new Particle(atomicInteger.incrementAndGet(), Double.parseDouble(dynamicFileList[0]),
//                            Double.parseDouble(dynamicFileList[1]),
//                            Double.parseDouble(dynamicFileList[2]),
//                            Double.parseDouble(dynamicFileList[3]),
//                            radiusList.get(i++)
//                    ));
//        }
//
//        return particleList;
//    }
}
