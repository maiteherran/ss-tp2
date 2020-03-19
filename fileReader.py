import pandas as pd
import numpy as np
import re
import matplotlib.pyplot as plt
import operator

class MeanError:
    def __init__(self, mean, stdev):
        self.mean = mean
        self.stdev = stdev


def graph_var_noise():
    final_va_map = {}  # file : (N : (ruido : Va))
    for i in range(2, 6):
        file = open('/Users/pedroremigiopingarilho/Desktop/ITBA/SS/ss-tp2/vaOutputNoise' + str(i) + '.txt', "r")

        n = -1
        flag = False
        va_values = []

        final_va_map[i] = {}

        for line in file.readlines():
            search = re.match(r'N: (.*) ruido: (.*)', line, re.M)
            if search:
                if int(search.group(1)) != int(n):
                    n = int(search.group(1))
                    final_va_map[i][n] = {}

                ruido = float(search.group(2))
                if flag:
                    final_va_map[i][n][ruido] = np.average(va_values)
                flag = True
                va_values = []
            else:
                va = re.match(r'(.*) (.*)', line, re.M)
                if va:
                    va_values.append(float(va.group(2)))

        file.close()

    n_for_graph_aux = {}  # N : (ruido : list(Va))
    n_for_graph = {}  # N : (ruido : myclass(mean stder))

    for number in final_va_map[2].keys():
        print(number)
        n_for_graph_aux[number] = {}
        n_for_graph[number] = {}
        for ruido in final_va_map[2][number]:
            n_for_graph_aux[number][ruido] = []
            n_for_graph[number][ruido] = MeanError

    for fil in final_va_map:  # para cada um dos arquivos
        for n in final_va_map[fil]:  # para cada um dos ns dentro do arquivo
            for ruido in final_va_map[fil][n]:  # para cada um dos ruidos dentro do arquivo
                n_for_graph_aux[n][ruido].append(final_va_map[fil][n][ruido])

    for n in n_for_graph_aux:
        for ruido in n_for_graph_aux[n]:
            va = n_for_graph_aux[n][ruido]
            n_for_graph[n][ruido] = MeanError(np.mean(va), np.std(va))

    for n in n_for_graph:
        x, y = zip(*n_for_graph[n].items())
        mean_list = list(map(operator.attrgetter("mean"), y))
        plt.errorbar(x, mean_list, yerr=list(map(operator.attrgetter("stdev"), y)),
                     label=n, fmt='-o')
    xmin, xmax = plt.xlim()
    plt.xlim(0.8, xmax)
    plt.legend(title='N')
    plt.xlabel("Va")
    plt.ylabel("Ruido")
    plt.title('Variacion de ruido con densidad = 4')
    plt.show()


def graph_var_density():
    file_final_values = {}  # File: (N : va)
    for i in range(1, 6):
        file = open('/Users/pedroremigiopingarilho/Desktop/ITBA/SS/ss-tp2/vaOutputDensity' + str(i) + '.txt', "r")

        va_values = []
        density = -1
        beginning = True

        file_final_values[i] = {}

        for line in file.readlines():
            search = re.match(r'N: (.*) densidad: (.*)', line, re.M)
            if search:
                if int(search.group(1)) != int(density):
                    density = int(search.group(1))
                    if not beginning:
                        file_final_values[i][density] = np.average(va_values)
                    beginning = False
            else:
                va = re.match(r'(.*) (.*)', line, re.M)
                if va:
                    aa = va.group(2)
                    va_values.append(float(va.group(2)))
        file.close()

    graph_map_aux = {}  # d : list(Va)
    graph_map = {}  # d : MeanError
    beginning = True

    for file in file_final_values:
        for density in file_final_values[file]:
            if beginning:
                graph_map_aux[density] = []
            graph_map_aux[density].append(file_final_values[file][density])
        beginning = False

    for density in graph_map_aux:
        graph_map[density] = MeanError(np.average(graph_map_aux[density]), np.std(graph_map_aux[density]))

    x = graph_map.keys()
    y = graph_map.values()
    mean_list = list(map(operator.attrgetter("mean"), y))
    plt.errorbar(x, mean_list, yerr=list(map(operator.attrgetter("stdev"), y)), fmt='-o')
    xmin, xmax = plt.xlim()
    plt.xlim(0.8, xmax)
    plt.ylabel("Va")
    plt.xlabel("Densidad")
    plt.title('Variacion de densidad con ruido = 1')
    plt.show()


graph_var_density()
