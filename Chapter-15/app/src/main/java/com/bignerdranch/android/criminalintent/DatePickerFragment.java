package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DatePickerFragment создается и настраиваетя экземпляр AlertDialog, отображающий виджет DatePicker.
 * В качестве хоста DatePickerFragment используется экземпляр CrimePagerActivity.
 * Экземпляр AlertDialog может отображаться и без DialogFragment, но Android так поступать не рекомендует.
 * Управление диалоговым окном из FragmentManager открывает больше возможностей для его отображения.
 * Кроме того, «минимальный» экземпляр AlertDialog исчезнет при повороте устройства.
 * С другой стороны, если экземпляр AlertDialog упакован во фрагмент,
 * после поворота диалоговое окно будет создано заново и появится на экране.
 * */
public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    private DatePicker mDatePicker;

    /**
     * Чтобы передать дату преступления DatePickerFragment, напишем метод
     * newInstance(Date) и сделаем объект Date аргументом фрагмента.
     * Чтобы вернуть новую дату фрагменту CrimeFragment для обновления уровня модели и его
     * собственного представления, мы упакуем ее как дополнение объекта
     * Intent и передадим этот объект Intent в вызове CrimeFragment.onActivityResult(…)
     *
     * Чтобы получить данные в DatePickerFragment, мы сохраним дату в пакете аргу-
     * ментов DatePickerFragment, где DatePickerFragment сможет обратиться к ней.
     * Создание аргументов фрагмента и присваивание им значений обычно выполняет-
     * ся в методе newInstance(), заменяющем конструктор фрагмента. Добавьте в файл
     * DatePickerFragment.java метод newInstance(Date).
     * */
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Когда пользователь нажимает кнопку положительного ответа в диалоговом окне, приложение
     * должно получить дату из DatePicker и отправить результат CrimeFragment.
     * */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        /**
         * onActivityResult используется для передачи интента целевому фрагменту
         * Activity.onActivityResult(…) вызывается ActivityManager для роди-
         * тельской активности при уничтожении дочерней активности. При работе с ак-
         * тивностями вы не вызываете Activity.onActivityResult(…) самостоятельно; это
         * делает ActivityManager. После того как активность получит вызов, экземпляр
         * FragmentManager активности вызывает Fragment.onActivityResult(…) для соот-
         * ветствующего фрагмента.
         */
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),resultCode, intent);
    }

    //FragmentManager активности-хоста вызывает этот метод в процессе вывода DialogFragment на экран
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**Экземпляр DatePickerFragment должен инициализировать DatePicker по инфор-
         мации, хранящейся в Date. Однако для инициализации DatePicker необходимо
         иметь целочисленные значения месяца, дня и года. Объект Date больше напомина-
         ет временную метку и не может предоставить нужные целые значения напрямую.
         Чтобы получить нужные значения, следует создать объект Calendar и использо-
         вать Date для определения его конфигурации. После этого вы сможете получить
         нужную информацию из Calendar.*/
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        //представления могут сохранять состояние между изменениями конфигура-
        //        ции, но только в том случае, если у них есть атрибут id.
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                //.setPositiveButton(android.R.string.ok, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }
}
