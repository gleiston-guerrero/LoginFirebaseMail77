package com.example.loginfirebasemail77.modelos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.loginfirebasemail77.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class adaptadorLista extends RecyclerView.Adapter<adaptadorLista.ViewHolder> {
    private List<paciente> Data;
    private LayoutInflater myinflater;
    private Context context;

    public adaptadorLista(List<paciente> itemList, Context context)
    {
        this.myinflater=LayoutInflater.from(context);
        this.context=context;
        this.Data=itemList;
    }
    @Override
    public  int getItemCount()
    { return Data.size();
    }
    @Override
    public adaptadorLista.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=myinflater.inflate(R.layout.elementos, null);
        return new adaptadorLista.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final  adaptadorLista.ViewHolder holder, final int position)
    {
        holder.bindData(Data.get(position));

    }
    public  void  setItems(List<paciente> items){Data=items;}
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Nombre,Nacimiento,Genero,macAdress,deciveName,nameTutor;
        String NombresCompletos="Sin nombre";
        ImageView imgPerfil;
        ViewHolder(View itemView)
        {
            super(itemView);
            Nombre=itemView.findViewById(R.id.txtNombre);
            imgPerfil=itemView.findViewById(R.id.imgFotoPaciente);
            Nacimiento=itemView.findViewById(R.id.TxtNacimiento);
            Genero=itemView.findViewById(R.id.txtGenero);
            macAdress=itemView.findViewById(R.id.txtMacdispositivo);
            deciveName=itemView.findViewById(R.id.txtDeciveName);
            nameTutor=itemView.findViewById(R.id.txtNameTutorV);


        }
        void bindData(final  paciente item)
        {
            if(item.getImagBase64().length()<=0)
            {
                Picasso.get().load("https://res.cloudinary.com/durxpegdm/image/upload/v1627940101/3d-flame-279_xt18fx.png").resize(100,100).centerCrop().into(imgPerfil);
            }else
            {
                Picasso.get().load(item.getImagBase64()).resize(100,100).centerCrop().into(imgPerfil);
            }
            NombresCompletos=item.getFirstname()+" "+item.getLastname();
            Nombre.setText(NombresCompletos);
            Nacimiento.setText("Nacimiento: "+item.getBirthname());
            Genero.setText("Genero: "+item.getGender());
            macAdress.setText("Dirección Mac: "+item.getMacadress());
            deciveName.setText("Nombre del dispositivo: "+item.getDecivename());
            nameTutor.setText("Nombre del tutor: "+item.getNameTutor());

        }
    }
}
