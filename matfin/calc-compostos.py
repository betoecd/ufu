# Calculadora Financeira MatFin
from time import sleep
from math import log

def calc_juros_simples_j(vp, i, n):
    return vp * i * n

def calc_juros_simples_vp(j, i, n):
    return j / (i * n)

def calc_juros_simples_i(j, vp, n):
    return j / (vp * n)

def calc_juros_simples_n(j, vp, i):
    return j / (vp * i)

def calc_juros_simples_vf(vp, i, n):
    juros = calc_juros_simples_j(vp, i, n)
    return vp + juros, juros

# def calc_juros_compostos_j(vp, i, n):
#     return vp *(((1 + i)**n) - 1)

def calc_juros_compostos_vp(vf, i, n):
    return vf / ((1 + i)**n)

def calc_juros_compostos_i(vf, vp, n):
    return (vf/vp)**(1/n) - 1

def calc_juros_compostos_n(vf, vp, i):
    return log(vf/vp)/log(1+i)

def calc_juros_compostos_vf(vp, i, n):
    vf = vp*(1+i)**n
    juros = vf - vp
    return vf, juros


def casting_values_juros_simples(a, b, c):
    """
    Função responsável por realizar casting dos valores da fórmula
    """
    try:
        a = float(a)
        b = float(b)
        c = float(c)
    except:
        global status
        status = -4
        return 1, 1, 1
    return a, b, c


def calc_juros_simples():
    """
    Função responsável por calcular juros simples
    """
    print('Informe os valores para cada parâmetro.')
    print('IMPORTANTE! A informação a ser encontrada deve ser deixada em branco.')
    j = input('Total de Juros (J): ')
    vp = input('Valor presente/Capital (VP): ')
    i = input('Taxa de juros (i): ')
    n = input('Número de períodos (n): ')
    print('-'*20)
    print('\n')

    # Verificando variável a ser encontrada
    if j == '':
        vp, i, n = casting_values_juros_simples(vp, i, n)
        vf, j = calc_juros_simples_vf(vp, i, n)
        return 'Valor Futuro = %.2f , Juros = %.2f' % (vf, j)
    elif vp == '':
        j, i, n = casting_values_juros_simples(j, i, n)
        vp = calc_juros_simples_vp(j, i, n)
        return 'Valor Presente = %.2f' % vp
    elif i == '':
        j, vp, n = casting_values_juros_simples(j, vp, n)
        i = calc_juros_simples_i(j, vp, n)
        return 'Taxa de juros(i) = %.2f' % i
    elif n == '':
        j, vp, i = casting_values_juros_simples(j, vp, i)
        n = calc_juros_simples_n(j, vp, i)
        return 'Períodos(n) = %.2f' % n
    else:
        global status
        status = -3


def check_unidade(unidade):
    """
    Verifica unidades de períodos válidos
    """
    if unidade not in ['a', 'm', 'd']:
        global status
        status = -5
        return False
    return unidade


def compare_and_convert(i, unidade_i, n, unidade_n):
    """
    Compara e converte unidades 
    """
    if unidade_i == unidade_n:
        return i, n
    elif unidade_i == 'd' and unidade_n == 'm':
        return i, str(float(n) * 30)
    elif unidade_i == 'd' and unidade_n == 'a':
        return i, str(float(n) * 360)
    elif unidade_i == 'm' and unidade_n == 'd':
        return str((1+float(i))**(1/30)-1), n
    elif unidade_i == 'm' and unidade_n == 'a':
        return i, str(float(n)*12)
    elif unidade_i == 'a' and unidade_n == 'd':
        return str((1+float(i))**(1/360)-1), n
    else:
        return str((1+float(i))**(1/12)-1), n


def calc_juros_compostos():
    """
    Função responsável por calcular juros compostos
    """
    print('Informe os valores para cada parâmetro.')
    print('IMPORTANTE! A informação a ser encontrada deve ser deixada em branco.')
    vf = input('Valor Final (VF): ')
    vp = input('Valor presente/Capital (VP): ')
    i = input('Taxa de juros (i): ')
    if i != '':
        print('Taxa de juros corresponde a qual período?')
        print('a = ano, m = meses, d =dias')
        unidade_i = input('Unidade taxa de juros: ')
    n = input('Número de períodos (n): ')
    if n != '':
        print('a = ano, m = meses, d =dias')
        unidade_n = input('Unidade período: ')
    print('-'*20)
    print('\n')

    # Verificando variável a ser encontrada
    if vf == '':
        # Validando unidades
        unidade_i = check_unidade(unidade_i)
        unidade_n = check_unidade(unidade_n)
        if not unidade_i or not unidade_n:
            return False

        i, n = compare_and_convert(i, unidade_i, n, unidade_n)


        vp, i, n = casting_values_juros_simples(vp, i, n)
        vf, j = calc_juros_compostos_vf(vp, i, n)
        return 'Valor Futuro = %.2f , Juros = %.2f' % (vf, j)

    elif vp == '':
        # Validando unidades
        unidade_i = check_unidade(unidade_i)
        unidade_n = check_unidade(unidade_n)
        if not unidade_i or not unidade_n:
            return False

        i, n = compare_and_convert(i, unidade_i, n, unidade_n)


        vf, i, n = casting_values_juros_simples(vf, i, n)
        vp = calc_juros_compostos_vp(vf, i, n)
        return 'Valor Presente = %.2f' % vp

    elif i == '':
         # Validando unidades
        unidade_n = check_unidade(unidade_n)
        if not unidade_n:
            return False

        vf, vp, n = casting_values_juros_simples(vf, vp, n)
        i = calc_juros_compostos_i(vf, vp, n)
        return 'Taxa de juros(i) = %.2f a%s' % (i, unidade_n)

    elif n == '':
        # Validando unidades
        unidade_i = check_unidade(unidade_i)
        if not unidade_i:
            return False

        vf, vp, i = casting_values_juros_simples(vf, vp, i)
        n = calc_juros_compostos_n(vf, vp, i)
        return 'Períodos(n) = %.2f %s' % (n, unidade_i)

    else:
        global status
        status = -3


switcher = {
    1: calc_juros_simples,
    2: calc_juros_compostos,
}

status_error = {
    -1: 'Entrada inválida para operação',
    -2: 'Valor não corresponde a uma operação, tente novamente',
    -3: 'Variável não foi definida',
    -4: 'Valores definidos de maneira incorreta',
    -42: 'Saindo do programa =/',
}

# Operação
operacao = 1

while operacao != 0:
    # Resetando staus da operação
    status = 0

    print('Qual operação você deseja realizar? Digite o valor correspondente')
    print('1 - Juros simples')
    print('2 - Juros compostos')
    print('-'*20)
    print('0 - Para sair do programa')
    print('='*20)
    operacao = input('Operação: ')
    print('\n')

    # Aplicando cast para inteiro
    try:
        operacao = int(operacao)
    except:
        status = -1

    # Validando valor da operação entre os permitidos
    if operacao == 0:
        status = -42
    elif operacao not in range(1, 3):
        status = -2

    if status >= 0:
        func = switcher.get(operacao, 'nothing')
        response = func()

        if status >= 0:
            print(response)
            sleep(5)
        else:
            print(status_error.get(status, ''))
    else:
        print(status_error.get(status, ''))

    print('\n\n')
    print('='*20)