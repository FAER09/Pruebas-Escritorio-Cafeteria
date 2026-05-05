package modelo;

public abstract class DecoradorCondimento extends Bebida {
    protected Bebida bebida;

    public DecoradorCondimento(Bebida bebida) {
        this.bebida = bebida;
    }
}
