package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import logic.SemesterManager;
import models.GradStudent;
import models.Student;
import models.UndergradStudent;

public class StorageManager {

    private static final String FILE_PATH = "data/grading_data.json";
    private static StorageManager instance;
    private final Gson gson;

    private StorageManager() {
        RuntimeTypeAdapterFactory<Student> studentAdapter =
            RuntimeTypeAdapterFactory.of(Student.class, "type")
                .registerSubtype(UndergradStudent.class, "UNDERGRAD")
                .registerSubtype(GradStudent.class, "GRAD");

        this.gson = new GsonBuilder()
            .registerTypeAdapterFactory(studentAdapter)
            .setPrettyPrinting()
            .create();
    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    public void save(SemesterManager semesterManager) {
        DataSaver saver = new DataSaver(FILE_PATH, gson);
        saver.save(semesterManager);
    }

    public SemesterManager load() {
        DataLoader loader = new DataLoader(FILE_PATH, gson);
        return loader.load();
    }
}
