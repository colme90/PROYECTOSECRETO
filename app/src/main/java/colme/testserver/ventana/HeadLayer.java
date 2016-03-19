package colme.testserver.ventana;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import colme.testserver.R;
import colme.testserver.Util.SemaforoUtil;

/**
 * Creates the head layer view which is displayed directly on window manager.
 * It means that the view is above every application's view on your phone -
 * until another application does the same.
 */
public class HeadLayer extends View {

    private Context mContext;
    private FrameLayout mFrameLayout;
    private WindowManager mWindowManager;
    private View v;



    private int tarjetaSeleccionada = -1;


    public HeadLayer(Context context) {
        super(context);

        mContext = context;
        mFrameLayout = new FrameLayout(mContext);

        addToWindowManager();
    }

    private void addToWindowManager() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);




        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want.
       v =  layoutInflater.inflate(R.layout.head, mFrameLayout);


        ImageView iv_tar1 = (ImageView) v.findViewById(R.id.imageView_tarjeta1);
        final TextView tv_tarjetaSeleccionada = (TextView) v.findViewById(R.id.textView_tarjetaSeleccionada);
        final TextView tv_fechaCad = (TextView) v.findViewById(R.id.textView_fechaCad);
        iv_tar1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tarjetaSeleccionada = 1;
                tv_tarjetaSeleccionada.setText("4907 XXXX XXXX 0027");
                tv_fechaCad.setText("12 / 17");
            }
        });

        ImageView iv_tar2 = (ImageView) v.findViewById(R.id.imageView_tarjeta2);
        iv_tar2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tarjetaSeleccionada = 2;
                tv_tarjetaSeleccionada.setText("4900 XXXX XXXX 0000");
                tv_fechaCad.setText("12 / 17");
            }
        });

        ImageView iv_tar3 = (ImageView) v.findViewById(R.id.imageView_tarjeta3);
        iv_tar3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tarjetaSeleccionada = 3;
                tv_tarjetaSeleccionada.setText("4111 XXXX XXXX 1111");
                tv_fechaCad.setText("12 / 17");
            }
        });


        Button bb= (Button) v.findViewById(R.id.pagar);
        bb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Clicked----><<<<<<<");

                if (tarjetaSeleccionada == -1){
                    Toast.makeText(mContext,"Es necesario seleccionar una tarjeta",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    switch(tarjetaSeleccionada) {
                        case 1:
                            editor.putString(mContext.getString(R.string.tarjeta), "{\n" +
                                    "\t\"tarjeta\": \"4907171010100027\",\n" +
                                    "\t\"cvv\": \"231\",\n" +
                                    "\t\"fecha\": \"1712\"\n" +
                                    "}");
                            break;
                        case 2:
                            editor.putString(mContext.getString(R.string.tarjeta), "{\n" +
                                    "\t\"tarjeta\": \"49000000000000\",\n" +
                                    "\t\"cvv\": \"231\",\n" +
                        "\t\"fecha\": \"1712\"\n" +
                        "}");
                            break;
                        case 3:
                            editor.putString(mContext.getString(R.string.tarjeta), "{\n" +
                                    "\t\"tarjeta\": \"41111111111111111\",\n" +
                                    "\t\"cvv\": \"231\",\n" +
                        "\t\"fecha\": \"1712\"\n" +
                        "}");
                            break;

                        default:
                            Toast.makeText(mContext,"Se ha producido un error",Toast.LENGTH_SHORT).show();
                            break;
                    }

                    editor.commit();

                    SemaforoUtil.LOCK.release();
                    destroy();
                }
            }
        });

        bb.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mWindowManager.addView(v, params);
    }




    /**
     * Removes the view from window manager.
     */
    public void destroy() {
        mWindowManager.removeView(v);
    }


}
