package com.example.mazrepartotienda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class  ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
{
    private List<list_element> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdapter(List<list_element> mData, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.context = context;
    }

    @Override
    public int getItemCount()
    { return mData.size();}

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType)
    {
        View view = mInflater.inflate(R.layout.list_element,null);
        return new ListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull final ListAdapter.ViewHolder holder, final int position)
    {
        holder.binData(mData.get(position));
        if(holder.iTipoPedido != 2)
        {
            Date currentTime = Calendar.getInstance().getTime();

            long lTimeMore = ((holder.iTiempoPedido*60)*1000);
            if (holder.timer != null) {
                holder.timer.cancel();
            }
            long timer =(holder.lHoraPedido+lTimeMore)-currentTime.getTime();

            //timer = timer*1000;

            if(timer>0) {
                holder.timer = new CountDownTimer(timer, 1000) {
                    public void onTick(long millisUntilFinished) {

                        int iSegundos= (int) (millisUntilFinished/1000);
                        int hours = iSegundos / 3600;
                        int minutes = (iSegundos % 3600) / 60;
                        int seconds = iSegundos % 60;

                        holder.hora.setText("Listo en: \n"+String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    }

                    public void onFinish() {
                        holder.hora.setText("AHORA");
                    }
                }.start();
            }
            else
            {
                holder.hora.setText("AHORA");
            }
        }
        else
        {
            holder.hora.setText("Finalizado");
        }
    }

    public void setItems(List<list_element> items)
    {mData=items;}

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView titulo;
        TextView dinero;
        TextView hora;
        int iTiempoPedido;
        long lHoraPedido;
        int iTipoPedido;
        CountDownTimer timer;

        ViewHolder(View itemView)
        {
            super(itemView);
            titulo= itemView.findViewById(R.id.m_Titulo);
            dinero= itemView.findViewById(R.id.m_dinero);
            hora= itemView.findViewById(R.id.m_hora);

        }

        void binData(@org.jetbrains.annotations.NotNull final list_element item)
        {
            titulo.setText(item.getTitulo());
            dinero.setText(item.getDinero());
            hora.setText("");
            iTiempoPedido=item.tiempoPedido;
            lHoraPedido=item.TiempoActualPedido;
            iTipoPedido=item.tipoPedido;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context, MainActivity_detalle.class);
                    Bundle extras = new Bundle();
                    extras.putString("keyPedido",item.PedidoKey);
                    extras.putString("keyRestaurante",item.RestauranteKey);
                    extras.putString("tipoPedido",String.valueOf(item.tipoPedido));
                    intent.putExtras(extras);
                    context.startActivity(intent);
                }
            });
        }

    }
}
