package mx.com.azteca.home.model.ipati.pojo;


public class Workflow {

    public int IdDocumento;
    public int Version;
    public int IdWorkflowStep;
    public String Tag;
    public String ActionText;
    public String StatusText;
    public boolean Optional;
    public int Orden;
    public int Icon;
    public int IdFormulario;
    public boolean Offline;

    private Formulario formulario;

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public Formulario getFormulario() {
        return formulario;
    }
}
