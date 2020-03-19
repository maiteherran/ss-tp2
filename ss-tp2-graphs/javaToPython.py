# from subprocess import Popen, PIPE, STDOUT, run
import subprocess
import shlex
import pandas as pd
import numpy as np

# from plotnine import *


for i in range(0, 1):
    noise = float(i + 1) * 0.2
    length = 20
    density = 4
    N = density * length ** 2
    shell_line = 'java -jar \"/Users/pedroremigiopingarilho/Desktop/ITBA/SS/ss-tp2/target/tp2-1.0-SNAPSHOT.jar\" -L=' \
                 + str(length) + ' -N=' + str(N) + ' -noise=' + str(noise) + ' -i=400 -Rc=' + str(length / 10)
    print(shell_line)
    parameter_list = ['-i=400', '-L=' + str(length), '-N=' + str(N), '-noise=' + str(noise), '-Rc=' + str(length / 10)]
    # calculation_values = subprocess.Popen(['java', '-jar',
    #                                        '/Users/pedroremigiopingarilho/Desktop/ITBA/SS/ss-tp2/target/tp2-1.0'
    #                                        '-SNAPSHOT.jar',
    #                                        '-L=' + str(length), '-N=' + str(N),
    #                                        '-noise=' + str(noise), '-i=400', '-Rc=' + str(length / 10)],
    #                                       stdout=subprocess.PIPE)
    calculation_values = subprocess.Popen(shlex.split(shell_line), stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

    va_array = []
    # print(parameter_list)
    # elapsed_time = calculation_values.communicate()[0].decode('utf-8').strip()
    for lines in calculation_values.stdout:
        print(lines)
        va_array.append(lines)
    # ggplot()
