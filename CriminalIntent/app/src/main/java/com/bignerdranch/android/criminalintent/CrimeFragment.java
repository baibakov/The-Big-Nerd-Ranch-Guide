package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    /**
     * Запущенная активность должна вернуть результат, поэтому мы передаем ин-
     * тент через startActivityForResult(…) вместе с кодом запроса.
     * */
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_DATE = 0;

    /**
     * Метод получает UUID. Создает пакет аргументовб создает экземпляр фрагмента и присоеденяет аргументы к фрагменту
     * */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Метод получает и обрабатывает результат от DatePickerFragment
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Определение полей, значения которых должны быть возвращены запросом.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Выполнение запроса - contactUri здесь выполняет функции условия "where"
            // Контактная информация совместно используется многими приложениями, поэтому Android
            // предоставляет расширенный API для работы с контактными данными через
            // ContentProvider. Экземпляры этого класса инкапсулируют базы данных и
            // предоставляют доступ к ним другим приложениям.
            // Обращение к ContentProvider осуществляется через ContentResolver.
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                // Проверка получения результатов
                if (c.getCount() == 0) {
                    return;
                }
                // Извлечение первого столбца данных - имени подозреваемого.
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        // Чтение дополнения и получение Crime
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                /**
                 * Для создания связи можно назначить CrimeFragment целевым фрагментом (target fragment) для DatePickerFragment.
                 * Эта связь будет автоматически восстановлена после того, как и CrimeFragment,
                 * и DatePickerFragment будут уничтожены и заново созданы ОС.
                 * */
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }

        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Ниже перечислены важнейшие составляющие интента, используемые для опре-
                 * деления выполняемой операции.
                 * 1) Выполняемое действие (action) — обычно определяется константами из клас-
                 * са Intent. Так, для просмотра URL-адреса используется константа Intent.
                 * ACTION_VIEW, а для отправки данных — константа Intent.ACTION_SEND.
                 * 2) Местонахождение данных — это может быть как ссылка на данные, находящие-
                 * ся за пределами устройства (скажем, URL веб-страницы), так и URI файла или
                 * URI контента, ссылающийся на запись ContentProvider.
                 * 3) Тип данных, с которыми работает действие, — тип MIME (например, text/html
                 * или audio/mpeg3). Если в интент включено местонахождение данных, то тип
                 * обычно удается определить по этим данным.
                 *      Необязательные категории — если действие указывает, что нужно сделать, ка-
                 * тегория обычно описывает, где, когда или как вы пытаетесь использовать опе-
                 * рацию. Android использует категорию android.intent.category.LAUNCHER для
                 * обозначения активностей, которые должны отображаться в лаунчере приложе-
                 * ний верхнего уровня. С другой стороны, категория android.intent.category.
                 * INFO обозначает активность, которая выдает пользователю информацию о па-
                 * кете, но не отображается в лаунчере.
                 * */
                Intent i = new Intent(Intent.ACTION_SEND);  // ACTION SEND для отправки текста
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                /**
                 * Создание списока выбора для отображения активностей,реагирующих на неявный интент.
                 **/
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        /**
         * Создание интента для выбора контакта
         * */
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        /**
         * PackageManager известно все о компонентах, установленных на устройстве
         * Android, включая все его активности. Вызывая resolveActivity(Intent, int), вы приказываете найти
         * активность, соответствующую переданному интенту. Флаг MATCH_DEFAULT_ONLY
         * ограничивает поиск активностями с флагом CATEGORY_DEFAULT (по аналогии с startActivity(Intent)).
         * Если поиск прошел успешно, возвращается экземпляр ResolveInfo, который сообщает
         * полную информацию о найденной активности. С другой стороны, если поиск вернул null,
         * все кончено — контактного приложения нет, поэтому бесполезная кнопка просто блокируется.
         * */
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        return v;
    }

    private String getCrimeReport() {
        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";

        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_no_suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}
