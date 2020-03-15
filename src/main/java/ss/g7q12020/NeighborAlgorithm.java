package ss.g7q12020;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NeighborAlgorithm {

    //hay un valor pero se cambia dependiendo del archivo de input
    static double LENGTH;
    //por ahora valor fijo, dsps ver de cambiar
    static double RC;
    static long N;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);


    //arg 0: stat
    //arg 1: dinamico
    //arg 2: borde circular
    //arg 3: rc
    public static Map<Long, Set<Long>> findNeighbors(String staticFile, String dynamicFile, boolean circularBorder, int rc) {

        List<Particle> particleList;
        try {
            RC = rc;
            particleList = getParticleData(staticFile, dynamicFile);
        } catch (Exception e) {
            System.out.println("No se pudo procesar los archivos y/o valor " +
                    "de RC. Ver si los parametros estan bien escritos");
            return null;//fixme
        }
        long startTime = System.nanoTime();
        Map<Long, Set<Long>> idNeighborsMap = cimAlgorithm(particleList, circularBorder);
        //Map<Long, Set<Long>> idNeighborsMap = bruteForceAlgorithm(particleList,circularBorder);
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Tiempo de ejecución: " + totalTime / 1000000 + " ms");

//        try {
//            writeReturnFile(idNeighborsMap);
//            long id = 24;
//            System.out.println("Se encontraron a los vecinos");
//        } catch (IOException e) {
//            System.out.println("No se pudo crear el archivo de retorno");
//        }
        return idNeighborsMap;
    }

    private static Map<Long, Set<Long>> bruteForceAlgorithm(List<Particle> particleList, boolean border) {
        Map<Long, Set<Long>> idNeighborsMap = new HashMap<>();
        for (int i = 0; i < particleList.size(); i++) {
            Set<Long> idList = new HashSet<>();
            for (int j = 0; j < particleList.size(); j++) {
                double xVal = particleList.get(i).getX() - particleList.get(j).getX();
                double yVal = particleList.get(i).getY() - particleList.get(j).getY();
                if (border) {
                    xVal = Math.min(xVal,
                            (LENGTH - particleList.get(i).getX() - particleList.get(j).getX()));
                    yVal = Math.min(yVal,
                            (LENGTH - particleList.get(i).getY() - particleList.get(j).getY()));
                }
                if (i != j && RC >= Math.sqrt(Math.pow(xVal, 2) + Math.pow(yVal, 2))
                        - particleList.get(j).getRadius() - particleList.get(i).getRadius()) {
                    idList.add(particleList.get(j).getId());
                }
            }
            idNeighborsMap.put(particleList.get(i).getId(), idList);
        }
        return idNeighborsMap;
    }

    private static Map<Long, Set<Long>> cimAlgorithm(List<Particle> particleList, boolean circularBorder) {
        double maxRadius = 0;

        for (Particle p : particleList) {
            maxRadius = Math.max(maxRadius, p.radius);
        }

        int m = (int) Math.floor(LENGTH / (RC + 2 * maxRadius));
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
                x -= LENGTH / (double) m;
                mx++;
            }
            while (y >= 0) {
                y -= LENGTH / (double) m;
                my++;
            }
            areaMatrix[mx - 1][my - 1].addParticle(p);
        }

        Map<Long, Set<Long>> idNeighborsMap = new HashMap<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                for (Particle particle : areaMatrix[i][j].getParticles()) {
                    Set<Long> neighList = findNeighborParticles(particle, areaMatrix, i, j, circularBorder);

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

    //todo meter lo de ovito en otra clase utils. Aca ahora solo sirve para encontrar a los vecinos
    private static void writeReturnFile(Map<Long, Set<Long>> idNeighborsMap) throws IOException {
        Files.write(Paths.get("./returnedNeighbors.txt"), () -> idNeighborsMap.entrySet().stream()
                .<CharSequence>map(idMap -> idMap.getKey().toString() + ' ' + idMap.getValue()
                        .stream().map(Object::toString).collect(Collectors.joining(" ")))
                .iterator());
    }

    private static void generateOvitofile(List<Particle> particleList, long targetParticleId, Set<Long> targetNeighboursIds) throws IOException {
        BufferedWriter xyzFile = new BufferedWriter(new FileWriter("./ovito"));
        xyzFile.write(String.valueOf(particleList.size()));
        xyzFile.newLine();
        xyzFile.newLine();
        String line;
        for (Particle particle : particleList) {
            if (particle.getId() == targetParticleId) {
                line = particle.getX() + " " +
                        particle.getY() + " " +
                        particle.getRadius() + " " +
                        "255" + " " + "100" + " " + "100";
            } else if (targetNeighboursIds.contains(particle.getId())) {
                line = particle.getX() + " " +
                        particle.getY() + " " +
                        particle.getRadius() + " " +
                        "50" + " " + "100" + " " + "100";
            } else {
                line = particle.getX() + " " +
                        particle.getY() + " " +
                        particle.getRadius() + " " +
                        "150" + " " + "100" + " " + "100";
            }
            xyzFile.write(line);
            xyzFile.newLine();
        }
        xyzFile.close();
    }

    private static Set<Long> findNeighborParticles(Particle particle, MatrixCell[][] areaMatrix, int x, int y, boolean border) {
        Set<Long> neighbors = new HashSet<>();

        for (Particle p : areaMatrix[x][y].getParticles()) {
            if (!p.equals(particle)) {
                double calculation = Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(particle.getY() - p.getY(), 2)) - p.getRadius() - particle.getRadius();
                if (RC >= calculation) {//veo si la dist es menor que rc
                    neighbors.add(p.getId());
                }
            }
        }

        if (y >= areaMatrix.length - 1) {
            if (border) {
                for (Particle p : areaMatrix[x][0].getParticles()) {
                    double yPos = (LENGTH - particle.getY()) - p.getY();
                    if (RC >= Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(yPos, 2)) - p.getRadius() - particle.getRadius()) {//veo si la dist es menor que rc
                        neighbors.add(p.getId());
                    }
                }
            }
        } else {
            for (Particle p : areaMatrix[x][y + 1].getParticles()) {
                if (RC >= Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(particle.getY() - p.getY(), 2)) - p.getRadius() - particle.getRadius()) {//veo si la dist es menor que rc
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
                        double xPos = (LENGTH - particle.getX()) - (p.getX());
                        double yPos = particle.getY() - p.getY();
                        if (aux != y) {
                            yPos = (LENGTH - particle.getY()) - p.getY();
                        }

                        double calculation = Math.sqrt(Math.pow(xPos, 2) + Math.pow(yPos, 2)) - p.getRadius() - particle.getRadius();
                        if (RC >= calculation) {//veo si la dist es menor que rc
                            neighbors.add(p.getId());
                        }
                    }
                }
            } else {
                if (border || aux == y) {
                    for (Particle p : areaMatrix[x + 1][aux + i].getParticles()) {
                        double yPos = particle.getY() - p.getY();
                        double calculation = Math.sqrt(Math.pow(particle.getX() - p.getX(), 2) + Math.pow(yPos, 2)) - p.getRadius() - particle.getRadius();
                        if (RC >= calculation) {//veo si la dist es menor que rc
                            neighbors.add(p.getId());
                        }
                    }
                } else if (border) {
                    System.out.println('a');
                }
            }
        }
        return neighbors;
    }

    private static List<Particle> getParticleData(String staticFile, String dynamicFile) throws Exception {
        BufferedReader staticBuffer = new BufferedReader(new FileReader(new File(staticFile)));
        String str = staticBuffer.readLine();
        if (str == null) {
            throw new Exception();
        }

        N = Long.parseLong(str);
        str = staticBuffer.readLine();
        if (str != null) {
            LENGTH = Double.parseDouble(str);
        } else {
            throw new Exception();
        }

        List<Double> radiusList = new ArrayList<>();
        while ((str = staticBuffer.readLine()) != null) {
            String[] listStatic = str.split(" ");
            radiusList.add(Double.parseDouble(listStatic[0]));
        }

        List<Particle> particleList = new ArrayList<>();
        BufferedReader dynamicBuffer = new BufferedReader(new FileReader(new File(dynamicFile)));
        str = dynamicBuffer.readLine();
        if (str == null) {//por ahora salteo t0
            throw new Exception();
        }

        String[] dynamicFileList;
        int i = 0;
        while ((str = dynamicBuffer.readLine()) != null && (dynamicFileList = str.split(" ")).length > 1) {
            //la segunda condicion es para agarrar apenas t0
            particleList.add(
                    new Particle(atomicInteger.incrementAndGet(), Double.parseDouble(dynamicFileList[0]),
                            Double.parseDouble(dynamicFileList[1]),
                            Double.parseDouble(dynamicFileList[2]),
                            Double.parseDouble(dynamicFileList[3]),
                            radiusList.get(i++)
                    ));
        }

        return particleList;
    }
}
