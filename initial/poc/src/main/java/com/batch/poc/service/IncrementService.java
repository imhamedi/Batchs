package com.batch.poc.service;

import org.springframework.stereotype.Service;

@Service
public class IncrementService {
    private int variable;

    public void incrementVariable() {
        variable = 0;
        for (int i = 0; i <= 10; i++) {
            variable = i;
            System.out.println("Incrément " + i + ": " + variable);
        }

        System.out.println("Ceci est juste pour un test. La valeur finale de la variable est : " + variable);
    }

    public int getVariable() {
        return variable;
    }

    public void multiplyVariableBy10(int variable) {
        this.variable = variable * 10;
        System.out.println("La variable * 10 est : " + this.variable);

    }

    public void divideVariableBy0(int variable) {
        this.variable = variable / 0;
        System.out.println("La variable * 10 est : " + this.variable);

    }
}
