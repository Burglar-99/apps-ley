package mx.com.azteca.home.view.ipati;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.CDocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.Field;
import mx.com.azteca.home.util.Componente;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.view.ReporteAcitivity;
import mx.com.azteca.home.view.ipati.fragment.PagerAdapter;
import mx.com.azteca.home.view.ipati.util.ContentView;


public class FormularioActivity extends BaseActivity  {

    private static final int SCALE_DELAY = 30;
    private LinearLayout mRowContainer;
    private RelativeLayout layout;
    private Toolbar toolbar;
    private CDocumentInstance cDocumentInstance;

    LinearLayout btnAnterior;
    LinearLayout btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        mRowContainer = (LinearLayout) findViewById(R.id.row_container);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (this.toolbar != null) {
            this.toolbar.setTitle("");
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> {
                FormularioActivity.this.onBackPressed();
            });
        }

        this.btnAnterior = (LinearLayout) findViewById(R.id.btnAnterior);
        this.btnSiguiente = (LinearLayout) findViewById(R.id.btnSiguiente);
        ImageView imgAnterior = (ImageView) findViewById(R.id.imgAnterior);
        ImageView imgSiguiente = (ImageView) findViewById(R.id.imgSiguiente);

        if (btnAnterior != null) {
            this.btnAnterior.setOnClickListener(v -> {
                String folio    = FormularioActivity.this.getData("Folio").toString();
                FormularioActivity.this.mostrarProgress("Cargando informaci\u00f3n");
                cDocumentInstance.previosStep(folio);
            });
        }

        if (btnSiguiente != null) {
            this.btnSiguiente.setOnClickListener(v -> {
                updateInfo(true);
            });
        }

        if (imgAnterior != null) {
            imgAnterior.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_navigate_before).color(Color.parseColor("#ff8a47")).actionBar());
        }

        if (imgSiguiente != null) {
            imgSiguiente.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_navigate_next).color(Color.parseColor("#ff8a47")).actionBar());
        }

        for (int indice = 1; indice < mRowContainer.getChildCount(); indice++) {
            View rowView = mRowContainer.getChildAt(indice);
            rowView.animate().setStartDelay(100 + indice * SCALE_DELAY).scaleX(1).scaleY(1);
        }

        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment: fragments) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            getSupportFragmentManager().executePendingTransactions();
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("IdForm")) {
                if (btnSiguiente != null) {
                    btnSiguiente.setVisibility(View.GONE);
                }

                if (btnAnterior != null) {
                    btnAnterior.setVisibility(View.GONE);
                }
            }
            super.onCreateActivity(FormularioActivity.class);
        }

        String userName = (String)getData(PreferenceDevice.USER_NAME.Key, "");
        String passwd = (String)getData(PreferenceDevice.USER_PASSWD.Key, "");
        String deviceID = (String)getData("IMEI", "");
        cDocumentInstance = new CDocumentInstance();
        cDocumentInstance.setContext(FormularioActivity.this);
        cDocumentInstance.setUserName(userName);
        cDocumentInstance.setPasswd(passwd);
        cDocumentInstance.setDeviceID(deviceID);
        cDocumentInstance.setContext(FormularioActivity.this);
        cDocumentInstance.subscribe(cDocumentInstance.new ListenerDocumentInstance(){
            @Override
            public void onLoadReporte(String file) {
                FormularioActivity.this.ocultarProgress();
                Intent intent = new Intent(FormularioActivity.this, ReporteAcitivity.class);
                intent.putExtra("FILE_NAME", file);
                startActivity(intent);
            }

            @Override
            public void onPreviosStep() {
                formularaioGuardado();
            }

            @Override
            public void onError(String error) {
                FormularioActivity.this.ocultarProgress();
                FormularioActivity.this.notificarUsuario("Error", error);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_formulario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                updateInfo(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(outState != null)
            saveInstacen(outState, mRowContainer);
        super.onSaveInstanceState(outState);
    }

    private void saveInstacen(Bundle outState, ViewGroup view) {
        for (int indice = 0; indice < view.getChildCount(); indice++) {
            View child = view.getChildAt(indice);
            if (child instanceof Componente.Tool) {
                Componente.Tool tool = (Componente.Tool) child;
                if (tool.getField() != null)
                    outState.putString(String.valueOf(tool.getField().Identity), String.valueOf(tool.getValue()));
            }
            if (child instanceof ViewGroup) {
                saveInstacen(outState, ((ViewGroup) child));
            }
        }
    }

    protected void setValue(ViewGroup view, int id, Object value) {
        if (view == null) {
            if (mRowContainer != null) {
                setValue(mRowContainer, id, value);
            }
        }
        if (view  != null) {
            for (int indice = 0; indice < view.getChildCount(); indice++) {
                View child = view.getChildAt(indice);
                if (child instanceof Componente.Tool) {
                    Componente.Tool tool = (Componente.Tool) child;
                    if (tool.getField() != null) {
                        if (tool.getField().Identity == id) {
                            tool.setValue(value);
                            return;
                        }
                    }
                }
                if (child instanceof ViewGroup) {
                    setValue(((ViewGroup) child), id, value);
                }
            }
        }
    }

    public HashMap<String, Field> loadField(HashMap<String, Field> info, ViewGroup view) throws Exception {
        if (view == null) {
            if (mRowContainer != null) {
                loadField(info, mRowContainer);
            }
        }
        if (view  != null) {
            for (int indice = 0; indice < view.getChildCount(); indice++) {
                View child = view.getChildAt(indice);
                if (child instanceof Componente.Tool || child instanceof ContentView) {
                    Componente.Tool tool = child instanceof Componente.Tool ? (Componente.Tool) child : ((ContentView) child).getTool();
                    if (tool.getField() != null) {
                        if (!tool.isValid()) {
                            throw new Exception("Informaci\u00f3n incompleta " + (tool.getField().Etiqueta == null ? "" : tool.getField().Etiqueta));
                        }
                        info.put(tool.getField().XmlPath + "/" + tool.getField().XmlTag, tool.getField());
                    }
                }
                if (child instanceof ViewGroup) {
                    loadField(info, ((ViewGroup) child));
                }
            }
        }
        return info;
    }

    @Override
    public void selectTool(ViewGroup view, int idField) {

        if (view == null) {
            super.selectTool(view, idField);
            if (mRowContainer != null) {
                selectTool(mRowContainer, idField);
            }
        }
        if (view  != null) {
            for (int indice = 0; indice < view.getChildCount(); indice++) {
                View child = view.getChildAt(indice);
                if (child instanceof Componente.Tool || child instanceof ContentView) {
                    Componente.Tool tool = child instanceof Componente.Tool ? (Componente.Tool) child : ((ContentView) child).getTool();
                    if (tool.getField() != null) {
                        if (tool.getField().Identity == idField) {
                            super.ToolSelected = tool;
                            return;
                        }
                    }
                }
                if (child instanceof ViewGroup) {
                    selectTool(((ViewGroup) child), idField);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mRowContainer.getChildCount() > 0) {
            for (int i = mRowContainer.getChildCount() - 1; i >= 0; i--) {
                View rowView = mRowContainer.getChildAt(i);
                ViewPropertyAnimator propertyAnimator = rowView.animate().setStartDelay((mRowContainer.getChildCount() - 1) * SCALE_DELAY).scaleX(0).scaleY(0);
                propertyAnimator.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAfterTransition();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    protected void formularaioGuardado() {
        int idDocumento     = (int) FormularioActivity.this.getData("IdDocumento");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("IdDocumento", idDocumento);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void formularioCompletado(String formulario) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("IdForm"      , (int)getData("IdForm"));
        returnIntent.putExtra("IdParent"    , (int)getData("IdParent"));
        returnIntent.putExtra("Row"         , (int)getData("Row"));
        returnIntent.putExtra("Formulario"  , formulario);
        returnIntent.putExtra("NamedPath"   , (String)getData("NamedPath"));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public View addView(View parent, String title) {
        LinearLayout mRowContainer = this.mRowContainer;
        if (parent != null) {
            mRowContainer = (LinearLayout) parent.findViewById(R.id.layout_container);
        }
        else {
            mRowContainer = (LinearLayout) mRowContainer.findViewById(R.id.layout_base);
        }

        LinearLayout child = (LinearLayout) View.inflate(getBaseContext(), R.layout.row_detailview, null);
        TextView titleView = (TextView) child.findViewById(R.id.title);
        titleView.setText(title);
        mRowContainer.addView(child);
        return child;
    }

    public View addView(View parent, View view) {
        ((LinearLayout)parent.findViewById(R.id.layout_container)).addView(view, parent.getLayoutParams());
        if (getBundle() != null) {
            if (view instanceof Componente.TextBox) {
                Componente.TextBox textBox = (Componente.TextBox) view;
                if (getBundle().containsKey(String.valueOf(textBox.getField().Identity))) {
                    textBox.setText(getBundle().getString(String.valueOf(textBox.getField().Identity)));
                }
            }
        }
        return parent;
    }

    public View addTabContainer(String title, final Listener listener) {
        RelativeLayout relativeLayout = layout;
        if (relativeLayout == null) {
            this.layout = (RelativeLayout) View.inflate(getBaseContext(), R.layout.activity_tab, null);
            if (mRowContainer.findViewById(R.id.scrollView) != null) {
                mRowContainer.removeView(mRowContainer.findViewById(R.id.scrollView));
            }
            mRowContainer.addView(layout);
        }

        final ViewPager viewPager = (ViewPager) this.layout.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) this.layout.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(title));
        tabLayout.setSaveEnabled(true);


        if (relativeLayout == null) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        final PagerAdapter adapter = relativeLayout == null ? new PagerAdapter(getSupportFragmentManager()) : (PagerAdapter)viewPager.getAdapter();
        adapter.addItem(new Fragment() {
            {
                setRetainInstance(true);
            }
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                final View viewFragment = inflater.inflate(R.layout.fragment_container, container, false);
                if (listener != null) {
                    listener.loadView(viewFragment);
                }
                return viewFragment;
            }
        });

        adapter.notifyDataSetChanged();

        if (relativeLayout == null) {
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        viewPager.setOffscreenPageLimit(adapter.getCount());

        return layout;
    }

    @Override
    protected Activity getAcivity() {
        return FormularioActivity.this;
    }

    public interface Listener {
        void loadView(View view);
    }


    public Context getContext() {
        return FormularioActivity.this;
    }


    @Override
    public void printReporte() {
        String imei         = FormularioActivity.this.getData("IMEI").toString();
        String user         = FormularioActivity.this.getData(PreferenceDevice.USER_NAME.Key).toString();
        String passwd       = FormularioActivity.this.getData(PreferenceDevice.USER_PASSWD.Key).toString();
        int idDocumento     = (int) FormularioActivity.this.getData("IdDocumento");
        long fecha          = (long) FormularioActivity.this.getData("Fecha");
        String folio        = FormularioActivity.this.getData("Folio").toString();
        FormularioActivity.this.mostrarProgress("Cargando informaci\u00f3n");
        cDocumentInstance.loadReporte(idDocumento, new Date(fecha), folio, user, passwd, imei);
    }


    @Override
    public void loadStartOptions() {
        if (btnAnterior != null)
            btnAnterior.setVisibility(View.GONE);
    }

    @Override
    public void loadEndOptoons() {
        com.github.clans.fab.FloatingActionButton print = (FloatingActionButton) findViewById(R.id.mnuItemImprimir);
        if (print != null) {
            print.setImageDrawable(new IconicsDrawable(FormularioActivity.this, GoogleMaterial.Icon.gmd_print).color(Color.WHITE).actionBar());
            print.setLabelText("Imprimir");
            print.setOnClickListener(view -> printReporte());
            print.setVisibility(View.VISIBLE);
        }

        if (btnSiguiente != null)
            btnSiguiente.setVisibility(View.GONE);
    }
}

