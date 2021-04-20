package com.example.recetapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Database.DBManager;
import Database.IngredientFacade;
import Database.RecipeFacade;
import Model.Ingrediente;
import Model.Receta;
import Utility.Validations;


public class AddRecipeFormActivity extends AppCompatActivity {
    //Constante para sacar una foto
    public static final int CAMERA_REQUEST_CODE = 100;
    //Constante para pedir los permisos de la camara
    public static final int CAMERA_PERM_CODE = 101;
    //Constante para abrir la galería
    public static final int GALLERY_REQUEST_CODE = 105;
    //Constante permisos galeria
    public static final int GALLERY_PERM_CODE = 106;
    //Ruta de la foto seleccionada
    private String currentPhotoPath;
    //HashMapIds componentes Ingredientes
    private HashMap<Integer, ArrayList<Integer>> identificadores;
    //identificador cada linea de ingrediente
    private int id;
    //posibles tipos de receta
    private static final List<String> TYPES = Arrays.asList("Postres", "Ensaladas", "Carnes", "Pescados","Pastas", "Otros");
    //posibles medidas
    private static final List<String> SIZES = Arrays.asList("g", "ml", "uds", "cucharadas", "cucharaditas","vasos","tazas");


    //Inicializa el formulario, con los eventos pertinentes, permitiendo añadir ingredientes dinámicamente y utilizar imagenes en el formulario
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe_form);
        id = Integer.MAX_VALUE - 100000;

        setRow();
        initializePhotoDialog();
        initializeTypeSpinner();
        initializeRecipeView();
        identificadores = new HashMap<>();

    }
    //Inicializa el Spinner de tipos
    private void initializeTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, TYPES);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.RecipeType);
        sItems.setAdapter(adapter);


    }
    //Inicializa el Spinner de medidas
    private void initializeSizesSpinner(int id) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, SIZES);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(id);
        sItems.setAdapter(adapter);


    }


    //Crea los componentes relativos a introducir un ingrediente
    private void setRow() {
        Button button = (Button) findViewById(R.id.AddIngredientBtn);


        button.setOnClickListener(new View.OnClickListener() {


            int generatedId;
            ArrayList<Integer> ids;

            @Override
            public void onClick(View v) {

                ids = new ArrayList<>();
                id += 1;


                TableLayout tableLayout = findViewById(R.id.IngredientContainer);
                TableRow tableRow = new TableRow(tableLayout.getContext());
                tableRow.setId(id);


                generatedId = View.generateViewId();
                ids.add(generatedId);


                EditText editTextCantidad = new EditText(tableLayout.getContext());
                editTextCantidad.setId(generatedId);
                editTextCantidad.setHint("Cantidad");
                editTextCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                generatedId = View.generateViewId();
                ids.add(generatedId);

                Spinner medidas = new Spinner(tableLayout.getContext());
                medidas.setId(generatedId);


                generatedId = View.generateViewId();
                ids.add(generatedId);

                EditText editTextIngrediente = new EditText(tableLayout.getContext());
                editTextIngrediente.setId(generatedId);
                editTextIngrediente.setHint("Ingrediente");
                editTextIngrediente.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);


                generatedId = View.generateViewId();
                ids.add(generatedId);

                MaterialButton btn = new MaterialButton(tableLayout.getContext());
                btn.setId(generatedId);
                btn.setText("-");

                identificadores.put(id, ids);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tableLayout.removeView(tableRow);
                        identificadores.remove(tableRow.getId());

                    }
                });


                tableRow.addView(editTextCantidad);
                tableRow.addView(medidas);
                tableRow.addView(editTextIngrediente);
                tableRow.addView(btn);

                tableLayout.addView(tableRow);



                initializeSizesSpinner(medidas.getId());

            }

            private int getGeneratedId(ArrayList<Integer> ids) {
                int generatedId;
                generatedId = View.generateViewId();
                ids.add(generatedId);
                return generatedId;
            }
        });
    }

    private int getGeneratedId(ArrayList<Integer> ids) {
        int generatedId;
        generatedId = View.generateViewId();
        ids.add(generatedId);
        return generatedId;
    }

    //Marca el layout que contiene el ImageView de forma que se genere un diálogo al pulsarlo
    private void initializePhotoDialog() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.PhotoLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhotoDialog();
            }
        });

    }


    //Se genera el diálogo que permite seleccionar la ubicación de la imagen (galería, cámara)
    private void setPhotoDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.PhotoSource)
                .setTitle(R.string.InsertPhoto);

        builder.setPositiveButton(R.string.Galery, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                checkGaleryPermissions();
            }
        });
        builder.setNegativeButton(R.string.Camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takePhoto();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    //comprueba que los permisos de la galeria han sido suministrados, en caso de que no, se piden al usuatio
    private void checkGaleryPermissions() {
        ImageView imgContainer = (ImageView) findViewById(R.id.MealPhoto);

        if (ContextCompat.checkSelfPermission(AddRecipeFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddRecipeFormActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, GALLERY_PERM_CODE);


        } else {
            takePhotoFromGallery();
        }

    }



    //Abre la galería
    private void takePhotoFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);

    }


    //comprueba que los permisos de la cámara han sido suministrados, en caso de que no, se piden al usuatio
    private void takePhoto() {
        ImageView imgContainer = (ImageView) findViewById(R.id.MealPhoto);

        if (ContextCompat.checkSelfPermission(AddRecipeFormActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddRecipeFormActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_PERM_CODE);


        } else {
            dispatchTakePictureIntent();
        }

    }


    //Comprueba que se han suministrado los permisos a la cámara si no se muestra un mensaje de advertencia
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Se necesitan los permisos para sacar fotos con la camara ", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == GALLERY_PERM_CODE){
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoFromGallery();
            } else {
                Toast.makeText(this, "Se necesitan los permisos para acceder a la galería ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Asigna el valor de la foto sacada o de la galería al ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = (ImageView) findViewById(R.id.MealPhoto);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(f));

            }

        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_" + getFileExt(contentUri);
                // Create the File where the photo should go



                Uri selectedImage =  data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        Bitmap imagenGaleria=BitmapFactory.decodeFile(picturePath);
                        imageView.setImageBitmap(imagenGaleria);
                        cursor.close();

                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {

                        }

                        try (FileOutputStream out = new FileOutputStream(currentPhotoPath)) {
                            imagenGaleria.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (IOException e) {
                            e.printStackTrace();
                        }





                    }

                }
            }
        }
    }

    //Extrae la extensión de la foto seleccionada en la galería
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    //Crea el archivo de imagen y giarda la ruta en la variable global currentPhotoPath
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //Abre la aplicación de cámara y guarda la foro en la ruta indicada
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    private Receta addReceta(){
        RecipeFacade recipe = new RecipeFacade(DBManager.getInstance(this));

        String tipo = ((Spinner)(findViewById(R.id.RecipeType))).getSelectedItem().toString();
        Integer numComensales = Integer.valueOf(((EditText)findViewById(R.id.numDiner)).getText().toString());
        String nombreReceta=((EditText)(findViewById(R.id.RecipeName))).getText().toString();
        String dir = ((EditText)findViewById(R.id.Directions)).getText().toString();

        Receta r = new Receta(nombreReceta,tipo,numComensales,dir,currentPhotoPath);
        Receta toret= recipe.createRecipe(r);

        IngredientFacade ingredientes=new IngredientFacade(DBManager.getInstance(this));

        for (ArrayList<Integer> clave : this.identificadores.values()) {
            Ingrediente ingrediente;
            Double cantidad = Double.valueOf(((EditText)findViewById(clave.get(0))).getText().toString());
            String medida = (((Spinner)findViewById(clave.get(1))).getSelectedItem().toString());;
            String nombreIngrediente = (((EditText)findViewById(clave.get(2))).getText().toString());

            ingrediente=new Ingrediente(cantidad,medida,nombreIngrediente);

            ingredientes.createIngredient(ingrediente,r.getId());

        }

        return toret;
    }



    //Se valida el formulario y en caso correcto se vuelve a la vista principal
    private void initializeRecipeView() {
        Button btn = (Button) findViewById(R.id.AddRecipeFormButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarFormulario()) {
                    Receta r;
                    r = addReceta();



                    Intent intent = new Intent(AddRecipeFormActivity.this, MainActivity.class);
                    intent.putExtra("variable_receta",(Integer) r.getId());
                    startActivity(intent);
                }
            }
        });


    }

    //Validación de los campos del formulario
    private boolean validarFormulario() {
        boolean toret;
        toret = Validations.validateEditText(R.id.numDiner, findViewById(R.id.numDiner).getRootView(), Validations.fieldType.num);
        toret &= Validations.validateEditText(R.id.Directions, findViewById(R.id.Directions).getRootView(), Validations.fieldType.text);
        toret &= Validations.validateEditText(R.id.RecipeName, findViewById(R.id.RecipeName).getRootView(), Validations.fieldType.text);

        if (this.identificadores.keySet().size() != 0) {

            for (ArrayList<Integer> clave : this.identificadores.values()) {
                toret &= Validations.validateEditText(clave.get(0), findViewById(clave.get(0)).getRootView(), Validations.fieldType.decimal);
                toret &= Validations.validateEditText(clave.get(2), findViewById(clave.get(2)).getRootView(), Validations.fieldType.text);


            }


        }else{
            toret = false;
            Toast.makeText(this,"Se necesita añadir ingredientes a la receta",Toast.LENGTH_SHORT);
        }
        return toret;
    }


}