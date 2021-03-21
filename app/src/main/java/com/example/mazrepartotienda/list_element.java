package com.example.mazrepartotienda;

public class list_element {
    public String Titulo;
    public String Dinero;
    public String PedidoKey;
    public String RestauranteKey;
    public long TiempoActualPedido;
    public int tiempoPedido;
    public int tipoPedido;

    public list_element(String titulo, String pedidoKey,String dinero, String sKeyRestaurante,long lTiempoPedido,int iTiempo,int iTipoPedido) {
        Titulo = titulo;
        PedidoKey = pedidoKey;
        Dinero=dinero;
        RestauranteKey=sKeyRestaurante;
        TiempoActualPedido=lTiempoPedido;
        tiempoPedido=iTiempo;
        tipoPedido=iTipoPedido;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getPedidoKey() {
        return PedidoKey;
    }

    public void setPedidoKey(String pedidoKey) {
        PedidoKey = pedidoKey;
    }

    public String getDinero() {
        return Dinero;
    }

    public void setDinero(String dinero) {
        Dinero = dinero;
    }

    public String geRestauranteKey() {
        return RestauranteKey;
    }

    public void setRestauranteKey(String restauranteKey) {
        RestauranteKey = restauranteKey;
    }

    public long getTiempoActualPedido() {
        return TiempoActualPedido;
    }

    public void setTiempoActualPedido(long timeRaw) {
        this.TiempoActualPedido = timeRaw;
    }

    public int getTiempoPedido() {
        return tiempoPedido;
    }

    public void setTiempoPedido(int tiempoPedido) {
        this.tiempoPedido = tiempoPedido;
    }

    public int getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(int tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

}