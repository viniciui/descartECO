package com.example.descarteco;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.example.descarteco.helper.Permissoes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationManager locationManager;
    private LocationListener locationListener; //receber atualizações do usuário

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        //obtém o suporte e notifica quando o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //Objeto responsável por gerenciar a localização do usuário
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { //informa quando a localização do usuário muda
                Log.d("Localização", "onLocationChanged: " + location.toString());

                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();

                /*
                Geocoding é o processo de transformar um end ou descrição de um local em lat/long
                Reverse Geocoding é o processo de transformar lat/long em um endereço
                 */
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> listaEndereco = geocoder.getFromLocation(latitude, longitude,1);
                    String stringEndereco = "RR. Luís Pedro de Oliveira, Mamanguape, Paraíba";
                    //List<Address> listaEndereco = geocoder.getFromLocationName(stringEndereco,1);
                    if(listaEndereco!= null && listaEndereco.size()>0){
                        Address endereco = listaEndereco.get(0);

                        Log.d("Local", "onLocationChaged" + endereco.getAddressLine(0));

                        Double lat = endereco.getLatitude();
                        Double lon = endereco.getLongitude();

                        mMap.clear();
                        // Add marcador em Rio Tinto, Paraiba, and move the camera.
                        LatLng localUsuario = new LatLng(lat, lon);
                /*
                * //Add evento de clique no mapa (toda vez que o usuário clicar cria um marcador no mapa, nesse caso um marker azul)
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Local")
                                .snippet("Descrição")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                });
                */

                        //add um marcador
                        mMap.addMarker(new MarkerOptions().position(localUsuario).title("Meu local"));
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)) ->MUDAR A COR DO MARCADOR

                        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(localUsuario).build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario, 15));

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { //o status do serviço de localiz. muda

            }

            @Override
            public void onProviderEnabled(String s) { //quando o usuário habilida o serviço de localiz.

            }

            @Override
            public void onProviderDisabled(String s) { //quando o usuário desabilita o serviço de localiz.

            }
        };
//recuperar localização do usuário
                /*
                1) Provedor da localização
                2) Tempo mínimo entre atualizações de localização (milesegundos)
                3) Distancia minima entre atualizações de localizações(metros)
                4) Location LISTENER (para receber as atualizações)
                 */
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    10,
                    locationListener
            );
        }

        mMap.setMyLocationEnabled(true); //botão de localização e ponto azul no mapa


        //Tipo do mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(-6.807682, -35.074728)).title("Marcador em Rio Tinto"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(rioTinto));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            //permission denied (negada)
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidarPermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                //recuperar localização do usuário
                /*
                1) Provedor da localização
                2) Tempo mínimo entre atualizações de localização (milesegundos)
                3) Distancia minima entre atualizações de localizações(metros)
                4) Location LISTENER (para receber as atualizações)
                 */
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            10,
                            locationListener
                    );
                }
            }
        }
    }

    private void alertaValidarPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}