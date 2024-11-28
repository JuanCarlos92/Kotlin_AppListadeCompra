package com.example.applistacompramejorada

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import com.example.applistacompramejorada.ui.theme.AppListaCompraMejoradaTheme
import com.example.applistacompramejorada.ui.theme.itimFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppListaCompraMejoradaTheme {
                ListaCompraMejorada()
            }
        }
    }
}
//clase que representa un ítem con [nombre, color, fecha]
data class Item(
    val name: String,
    val color: Color,
    val date: String,
)

//Función guardar la lista al JSON
fun Context.saveListToJson(itemList: List<Item>, fileName: String = "lista_compras.json") {
    val file = File(filesDir, fileName) //Crea archivo en directorio
    val json = Gson().toJson(itemList) //Convierte lista a JSON
    file.writeText(json) //Escribe en el archivo
}

//Función cargar la lista del JSON
fun Context.loadListFromJson(fileName: String = "lista_compras.json"): List<Item> {
    val file = File(filesDir, fileName) //Crea referencia al archivo
    if (!file.exists()) return emptyList() //Si no existe....retorna vacio
    val json = file.readText() //Lee contenido del JSON
    val type = object : TypeToken<List<Item>>() {}.type //Define tipo genérico
    return Gson().fromJson(json, type) //Convierte JSON a lista de ítems
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCompraMejorada(context: Context = LocalContext.current) {

    //Variables
    var isDarkTheme by remember { mutableStateOf(false) } // Controlar el tema claro/oscuro
    val itemList = remember { mutableStateListOf<Item>() } // Lista de elementos
    var showDialog by remember { mutableStateOf(false) } // Estado del diálogo de añadir
    var showDialogCalendar by remember { mutableStateOf(false) } // Estado del diálogo del calendario
    var itemName by remember { mutableStateOf("") } // Nombre del elemento a añadir
    var itemColor by remember { mutableStateOf(Color.Transparent) } // Color del elemento
    var itemToDelete: Item? by remember { mutableStateOf(null) } // Elemento a eliminar
    var confirmDelete by remember { mutableStateOf(false) } // Confirmación de eliminación
    var selectDate by remember { mutableStateOf("") } // Fecha seleccionada

    //Variable para cambiar a modo oscuro o claro
    val colors = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colors) {
        Scaffold(
            topBar = {
                TopAppBar( //BARRA SUPERIOR (TopAppBar)
                    title = { Text("Lista de Compra") },
                    actions = {
                        //Switch para tema claro y oscuro
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(if (isDarkTheme) "Tema Claro" else "Tema Oscuro")
                            Switch( //Indica si el tema actual es oscuro --> Cambia el estado del tema al ser desactivado/activado
                                checked = isDarkTheme,
                                onCheckedChange = { isDarkTheme = it }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                //Botones flotantes [contador, calendario y agregar]
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    //Contador de elementos en la lista
                    FloatingActionButton(onClick = {}, containerColor = MaterialTheme.colorScheme.primary) {
                        Text(
                            text = itemList.size.toString(),
                            fontSize = 40.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontFamily = itimFamily,
                        )
                    }
                    //Botón para seleccionar fecha
                    FloatingActionButton(onClick = { showDialogCalendar = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }

                    //Botón para añadir nuevo elemento
                    FloatingActionButton(onClick = { showDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Añadir Elemento")
                    }
                }
            },
            bottomBar = {
                //Botones para guardar y cargar JSON
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    //Boton guardar JSON
                    Button(onClick = { saveItemsToJson(itemList, context)
                            Toast.makeText(context, "Lista guardada", Toast.LENGTH_SHORT).show() // Mostrar mensaje emergente
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Guardar JSON")
                    }
                    //Boton guardar JSON
                    Button(onClick = { loadItemsFromJson(itemList, context)
                        Toast.makeText(context, "Lista cargada", Toast.LENGTH_SHORT).show() // Mostrar mensaje emergente
                        }) {
                        Text("Cargar JSON")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding) //Aplicamos padding
            ) {
                if (itemList.isEmpty()) { //Si la itemList es vacia.... NO HAY ELEMENTOS
                    Text(text = "No hay elementos en la lista.", modifier = Modifier.padding(16.dp))
                } else {//Lista de elementos usando LazyColumn
                    LazyColumn {
                        items(itemList) { item -> //Itera sobre cada elemento de la lista

                            val (name, color, date) = item //Obtiene el nombre, color y fecha en las variables

                            Row(//Crea una fila x elemento
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = color)
                                Spacer(Modifier.width(8.dp))
                                //Texto: Nombre-fecha
                                Text(text = "$name - $date")

                                IconButton(onClick = {
                                    itemToDelete = item
                                    confirmDelete = true
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar elemento")
                                }
                            }
                        }
                    }
                }
                // Cuadro de diálogo de confirmación de eliminación
                if (confirmDelete && itemToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { confirmDelete = false },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Estás seguro de que deseas eliminar '${itemToDelete!!.name}'?") },
                        confirmButton = {
                            TextButton(onClick = {
                                itemList.remove(itemToDelete)
                                confirmDelete = false
                                itemToDelete = null
                            }) { Text("Confirmar") }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                confirmDelete = false
                                itemToDelete = null
                            }) { Text("Cancelar") }
                        }
                    )
                }
            }

            // Cuadro de diálogo para añadir nuevo elemento
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Añadir elemento") },
                    text = {
                        Column {
                            TextField(
                                value = itemName,
                                onValueChange = { itemName = it },
                                label = { Text("Introduce el nombre") }
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Fecha seleccionada: $selectDate")
                            Button(onClick = { showDialogCalendar = true }) {
                                Text("Seleccionar fecha")
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Selecciona un Color:")
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                val colorsList = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan)
                                colorsList.forEach { color ->
                                    Box(
                                        modifier = Modifier.size(40.dp).background(color)
                                            .clickable { itemColor = color }
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (itemName.isNotEmpty() && itemColor != Color.Transparent && selectDate.isNotEmpty()) {
                                itemList.add(Item(name = itemName, color = itemColor, date = selectDate))
                                itemName = ""
                                itemColor = Color.Transparent
                                selectDate = ""
                                showDialog = false
                            } else {
                                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        }) { Text("Añadir") }
                    }
                )
            }

            //IF calendario es  true  (Pulsa el boton....) entonces....
            if (showDialogCalendar) {
                val datePickerState = rememberDatePickerState()
                // Diálogo modal para seleccionar la fecha
                AlertDialog(
                    onDismissRequest = { showDialogCalendar = false },
                    title = { Text("Seleccionar fecha") },
                    text = {
                        Column {
                            DatePicker(state = datePickerState, showModeToggle = false)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            // Verificamos si hay una fecha seleccionada
                            val selectedDate = datePickerState.selectedDateMillis
                            if (selectedDate != null) {
                                //Formatear la fecha sin GTM 2024
                                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                selectDate =
                                    dateFormat.format(Date(selectedDate)) // Convertir a cadena sin GTM 2024
                            }
                            showDialogCalendar = false // Cerrar el diálogo del calendario
                        }) {
                            Text("Seleccionar fecha")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialogCalendar = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

//Función para guardar los ítems en JSON
fun saveItemsToJson(items: List<Item>, context: Context) {
    context.saveListToJson(items) //Llama a la funcion saveListToJson para guardar en formato JSON
}

// Función para cargar los ítems desde JSON
fun loadItemsFromJson(itemList: MutableList<Item>, context: Context) {
    itemList.clear() //Limpia la lista para preparar de nuevo el JSON
    itemList.addAll(context.loadListFromJson()) //Agregatodo el contenido del JSON a la lista de la App
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppListaCompraMejoradaTheme {
        ListaCompraMejorada()
    }
}
