package br.com.motiva.model;

// Contrato para trechos equipados com sensores. Mantida enxuta (ISP):
// apenas o comportamento de transmissao de dados, sem acoplar a hierarquia.
public interface MonitoravelViaIoT {

    void transmitirDadosSensor(double crescimentoMedido);
}
