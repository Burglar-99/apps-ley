package mx.com.azteca.home.view.ipati;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.CDocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.Column;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.util.Functions;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.view.FichaPagoActivity;
import mx.com.azteca.home.view.MetodoPagoActivity;
import mx.com.azteca.home.view.ReporteAcitivity;
import mx.com.azteca.home.view.ipati.itemanimator.CustomItemAnimator;
import mx.com.azteca.home.ui.MapDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class DocManagerActivity extends BaseActivity {

    final int PAGO_ACTIVITY         = 1001;
    final int REFERENCIA_ACTIVITY   = 1002;

    private int idDocumento;
    private int version;

    private DocumentoAdapter docAdapter;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private CDocumentInstance controller;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DocumentoAsignado docInfo;
    private CDocumentInstance cDocumentInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_doc_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle("Admon. Estimaci\u00F3n");
           //if (getIntent().getExtras().containsKey("documentoName")) {
           //    toolbar.setTitle(getIntent().getExtras().getString("documentoName"));
           //}
           //else {
                toolbar.setTitle("Estimaci\u00F3n");
            //}
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> {
                DocManagerActivity.this.onBackPressed();
            });
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

       // if (getIntent().getExtras().containsKey("documento")) {
            this.idDocumento    = 9;//getIntent().getExtras().getInt("documento");
            this.version        = 1;//getIntent().getExtras().getInt("version");
            super.onCreateActivity(DocManagerActivity.class);
        //}

        TextView lblCreate = (TextView) findViewById(R.id.lblCreate);

        if (lblCreate != null) {
            lblCreate.setOnClickListener(view -> {
                controller.createInstance();
            });
        }
        mProgressBar        = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView       = (RecyclerView) findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        this.docAdapter     = new DocumentoAdapter(new ArrayList<>(), R.layout.row_application_detail);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(() -> DocManagerActivity.super.loadWorkFlowWithInstances(idDocumento, version));

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.INVISIBLE);


        String userName = (String)getData(PreferenceDevice.USER_NAME.Key, "");
        String passwd = (String)getData(PreferenceDevice.USER_PASSWD.Key, "");
        String deviceID = (String)getData("IMEI", "");

        cDocumentInstance = new CDocumentInstance();
        cDocumentInstance.setContext(DocManagerActivity.this);
        cDocumentInstance.setUserName(userName);
        cDocumentInstance.setPasswd(passwd);
        cDocumentInstance.setDeviceID(deviceID);
        cDocumentInstance.setContext(DocManagerActivity.this);
        cDocumentInstance.subscribe(cDocumentInstance.new ListenerDocumentInstance(){
            @Override
            public void solcitarPago(DocumentoAsignado docAsignado) {
                Intent intent = new Intent(DocManagerActivity.this, MetodoPagoActivity.class);
                intent.putExtra("DOCUMENT_FOLIO", docAsignado.Folio);
                startActivityForResult(intent, PAGO_ACTIVITY);
            }

            @Override
            public void mostrarReferencia(DocumentoAsignado docAsignado) {
                Intent intent = new Intent(DocManagerActivity.this, FichaPagoActivity.class);
                intent.putExtra("DOCUMENT_FOLIO", docAsignado.Folio);
                startActivityForResult(intent, REFERENCIA_ACTIVITY);
            }

            @Override
            public void iniciarEdicion(DocumentoAsignado docAsignado) {
                DocManagerActivity.this.iniciarEdicion(docAsignado);
            }

            @Override
            public void onLoadReporte(String file) {
                DocManagerActivity.this.ocultarProgress();
                Intent intent = new Intent(DocManagerActivity.this, ReporteAcitivity.class);
                intent.putExtra("FILE_NAME", file);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                DocManagerActivity.this.ocultarProgress();
                DocManagerActivity.this.notificarUsuario("Error", error);
            }

            @Override
            public void notificarUsuario(String title, String message) {
                DocManagerActivity.this.notificarUsuario(title, message);
            }

            @Override
            public void mostrarProgress(String message) {
                DocManagerActivity.this.mostrarProgress(message);
            }

            @Override
            public void ocultarProgress() {
                DocManagerActivity.this.ocultarProgress();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String user     = DocManagerActivity.this.getData(PreferenceDevice.USER_NAME.Key).toString();
            String passwd   = DocManagerActivity.this.getData(PreferenceDevice.USER_PASSWD.Key).toString();
            String imei     = DocManagerActivity.this.getData("IMEI").toString();
            cDocumentInstance.verificarDocumento(docInfo.IdDocumento, docInfo.Fecha, docInfo.Folio);
        }
        DocManagerActivity.super.loadWorkFlowWithInstances(idDocumento, version);
    }

    @Override
    protected void loadDocumentoAsignado(List<DocumentoAsignado> listDocumento) {
        if (!(mRecyclerView.getAdapter() instanceof DocumentoAdapter)) {
            mRecyclerView.setAdapter(docAdapter);
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        docAdapter.clearDocumentos();
        docAdapter.addDocumentos(listDocumento);
        docAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadWorkflow(List<WorkFlowStep> list) {
        if (!(mRecyclerView.getAdapter() instanceof SectionedRecyclerViewAdapter)) {
            sectionAdapter = new SectionedRecyclerViewAdapter();
            mRecyclerView.setAdapter(sectionAdapter);
        }
        sectionAdapter.removeAllSections();
        for (WorkFlowStep step : list) {
            sectionAdapter.addSection(new WorkflowSection(step.Tag, step.getDocsAsignados()));
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        sectionAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class DocumentoAdapter extends RecyclerView.Adapter<DocumentoAdapter.ViewHolder> {

        private List<DocumentoAsignado> documentos;
        private int rowLayout;

        private DocumentoAdapter(List<DocumentoAsignado> documentos, int rowLayout) {
            this.documentos = documentos;
            this.rowLayout = rowLayout;
            Log.d("documentos 1", String.valueOf(documentos));
        }

        void clearDocumentos() {
            int size = this.documentos.size();
            Log.d("documentos size", String.valueOf(size));
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    documentos.remove(0);
                }
                this.notifyItemRangeRemoved(0, size);
            }
            notifyDataSetChanged();
        }

        void addDocumentos(List<DocumentoAsignado> documentos) {
            Log.d("documentos add", String.valueOf(documentos));
            this.documentos.addAll(documentos);
            this.notifyItemRangeInserted(0, documentos.size() - 1);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int index) {
            DocManagerActivity.this.docInfo = documentos.get(index);
            viewHolder.name.setText(docInfo.Folio);
            viewHolder.image.setImageDrawable(new IconicsDrawable(DocManagerActivity.this, GoogleMaterial.Icon.gmd_library_books).color(Color.parseColor("#ffab40")).actionBar());
            viewHolder.itemView.setOnClickListener(view -> {
                String user     = DocManagerActivity.this.getData(PreferenceDevice.USER_NAME.Key).toString();
                String passwd   = DocManagerActivity.this.getData(PreferenceDevice.USER_PASSWD.Key).toString();
                String imei     = DocManagerActivity.this.getData("IMEI").toString();
                cDocumentInstance.verificarDocumento(docInfo.IdDocumento, docInfo.Fecha, docInfo.Folio);
            });

            viewHolder.table.removeAllViews();
            for (Column column : docInfo.getVersionInfo().getListColumns()) {
                LinearLayout layout = (LinearLayout) DocManagerActivity.this.getLayoutInflater().inflate(R.layout.list_item_column_info, null);
                ((TextView)layout.findViewById(R.id.lblHeader)).setText(column.headerText);
                String value = column.value;
                if (column.value != null && Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}").matcher(column.value).find()) {
                    long time = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(value);
                    String dataFormated;
                    if (value.endsWith("00:00:00")) {
                        dataFormated = Functions.getInstance(Functions.Format.DATE).applyTime(time);
                    }
                    else {
                        dataFormated = Functions.getInstance(Functions.Format.DATE_TIME).applyTime(time);
                    }
                    ((TextView)layout.findViewById(R.id.lblValue)).setText(dataFormated);
                }
                else {
                    ((TextView)layout.findViewById(R.id.lblValue)).setText(value);
                }
                viewHolder.table.addView(layout);
            }

            if (docInfo.getDocumentInstance() != null && docInfo.getDocumentInstance().EndStep  < new Date().getTime()) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FF9634"));
            }
            viewHolder.imgLocalizacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    try {
                        view.setEnabled(false);
                        double zoom =  getZoom();
                        MapDialog mapDialog = new MapDialog(getAcivity(), (docInfo.WorkflowStep != null ? docInfo.WorkflowStep : "")) {
                            public AppCompatActivity getAppCompactActivity() throws Exception {
                                return DocManagerActivity.this;
                            }

                            @Override
                            public void setOnDismissListener(OnDismissListener listener) {
                                super.setOnDismissListener(listener);
                                view.setEnabled(true);
                            }
                        };
                        mapDialog.setLocation(docInfo.Folio, docInfo.Latitud, docInfo.Longitud);
                        mapDialog.show();

                        LatLng latLng = new LatLng(docInfo.getDocumentInstance().Latitud, docInfo.getDocumentInstance().Longitud);
                        mapDialog.setCenter(latLng, zoom);
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                        view.setEnabled(false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return documentos == null ? 0 : documentos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public ImageView image;
            public LinearLayout table;
            public ImageView imgLocalizacion;

            private ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.lblDocumento);
                image = (ImageView) itemView.findViewById(R.id.countryImage);
                table = (LinearLayout) itemView.findViewById(R.id.layout_row);
                imgLocalizacion = (ImageView) itemView.findViewById(R.id.imgLocaion);
                imgLocalizacion.setImageDrawable(new IconicsDrawable(DocManagerActivity.this, GoogleMaterial.Icon.gmd_location_on).color(Color.parseColor("#ffab40")).actionBar());
            }

        }
    }

    private class WorkflowSection extends StatelessSection {

        private String title;
        private List<DocumentoAsignado> list;

        private WorkflowSection(String title, List<DocumentoAsignado> list) {
            super(R.layout.list_item_title, R.layout.row_application_detail);
            this.title = title;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.name.setText(list.get(position).Folio);
            viewHolder.itemView.setOnClickListener(view -> {
                DocManagerActivity.this.docInfo = list.get(position);
                String user     = DocManagerActivity.this.getData(PreferenceDevice.USER_NAME.Key).toString();
                String passwd   = DocManagerActivity.this.getData(PreferenceDevice.USER_PASSWD.Key).toString();
                String imei     = DocManagerActivity.this.getData("IMEI").toString();
                cDocumentInstance.verificarDocumento(docInfo.IdDocumento, docInfo.Fecha, docInfo.Folio);
            });
            viewHolder.table.removeAllViews();
            for (Column column : list.get(position).getVersionInfo().getListColumns()) {
                if (column.headerText == null || column.value == null)
                    continue;

                LinearLayout layout = (LinearLayout) DocManagerActivity.this.getLayoutInflater().inflate(R.layout.list_item_column_info, null, false);
                ((TextView)layout.findViewById(R.id.lblHeader)).setText(column.headerText);
                String value = column.value;
                if (Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}").matcher(column.value).find()) {
                    long time = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(value);
                    String dataFormated;
                    if (value.endsWith("00:00:00")) {
                        dataFormated = Functions.getInstance(Functions.Format.DATE).applyTime(time);
                    }
                    else {
                        dataFormated = Functions.getInstance(Functions.Format.DATE_TIME).applyTime(time);
                    }
                    ((TextView)layout.findViewById(R.id.lblValue)).setText(dataFormated);
                }
                else {
                    ((TextView)layout.findViewById(R.id.lblValue)).setText(value);
                }
                viewHolder.table.addView(layout);
            }

            if (list.get(position).getDocumentInstance() != null && list.get(position).getDocumentInstance().EndStep  < new Date().getTime()) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FDECEC"));
            }
            viewHolder.imgLocalizacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    try {
                        view.setEnabled(false);
                        double zoom =  getZoom();
                        MapDialog mapDialog = new MapDialog(getAcivity(), (list.get(position).WorkflowStep != null ? list.get(position).WorkflowStep : "")) {
                            public AppCompatActivity getAppCompactActivity() throws Exception {
                                return DocManagerActivity.this;
                            }

                            @Override
                            public void setOnDismissListener(OnDismissListener listener) {
                                super.setOnDismissListener(listener);
                                view.setEnabled(true);
                            }
                        };
                        mapDialog.setLocation(list.get(position).Folio, list.get(position).Latitud, list.get(position).Longitud);
                        mapDialog.show();
                        LatLng latLng = new LatLng(list.get(position).Latitud, list.get(position).Longitud);
                        mapDialog.setCenter(latLng, zoom);
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                }
            });

            viewHolder.imgPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DocManagerActivity.this.mostrarProgress("Cargando informaci\u00f3n");
                        Object object = list.get(position);
                        if (object != null) {
                            String imei     = DocManagerActivity.this.getData("IMEI").toString();
                            String user     = DocManagerActivity.this.getData(PreferenceDevice.USER_NAME.Key).toString();
                            String passwd   = DocManagerActivity.this.getData(PreferenceDevice.USER_PASSWD.Key).toString();
                            DocumentoAsignado documentoAsignado = (DocumentoAsignado) object;
                            cDocumentInstance.loadReporte(documentoAsignado.IdDocumento, new Date(documentoAsignado.Fecha), documentoAsignado.Folio, user, passwd, imei);
                        }
                    }
                    catch (Exception ex) {
                        DocManagerActivity.this.ocultarProgress();
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvTitle.setText(title);
        }


    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public HeaderViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.lblNombre);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public LinearLayout table;
        public ImageView imgLocalizacion;
        public ImageView imgPrint;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.lblDocumento);
            image = (ImageView) itemView.findViewById(R.id.countryImage);
            table = (LinearLayout) itemView.findViewById(R.id.layout_row);
            imgLocalizacion = (ImageView) itemView.findViewById(R.id.imgLocaion);
            imgPrint = (ImageView) itemView.findViewById(R.id.imgPrint);
            imgLocalizacion.setImageDrawable(new IconicsDrawable(DocManagerActivity.this, GoogleMaterial.Icon.gmd_location_on).color(Color.parseColor("#007ee4")).actionBar());
            imgPrint.setImageDrawable(new IconicsDrawable(DocManagerActivity.this, GoogleMaterial.Icon.gmd_picture_as_pdf).color(Color.parseColor("#007ee4")).actionBar());
        }
    }

}
