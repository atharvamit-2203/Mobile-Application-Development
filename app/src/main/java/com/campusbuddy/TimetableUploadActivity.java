package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// Upload timetable from photo, PDF, or Excel
public class TimetableUploadActivity extends Activity {

    private Button btnUploadPhoto, btnUploadPDF, btnUploadExcel, btnManualEntry;
    private TextView tvStatus, tvInstructions;
    private ImageView ivPreview;
    private LinearLayout previewLayout;
    
    private Uri selectedFileUri;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PDF_PICK = 2;
    private static final int REQUEST_EXCEL_PICK = 3;
    private static final int REQUEST_PERMISSION = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_upload);
        
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnUploadPDF = findViewById(R.id.btnUploadPDF);
        btnUploadExcel = findViewById(R.id.btnUploadExcel);
        btnManualEntry = findViewById(R.id.btnManualEntry);
        tvStatus = findViewById(R.id.tvStatus);
        tvInstructions = findViewById(R.id.tvInstructions);
        ivPreview = findViewById(R.id.ivPreview);
        previewLayout = findViewById(R.id.previewLayout);
        
        // Request permissions on start
        checkAndRequestPermissions();
        
        btnUploadPhoto.setOnClickListener(v -> {
            if (hasStoragePermission()) {
                pickImage();
            } else {
                checkAndRequestPermissions();
            }
        });
        
        btnUploadPDF.setOnClickListener(v -> {
            if (hasStoragePermission()) {
                pickPDF();
            } else {
                checkAndRequestPermissions();
            }
        });
        
        btnUploadExcel.setOnClickListener(v -> {
            if (hasStoragePermission()) {
                pickExcel();
            } else {
                checkAndRequestPermissions();
            }
        });
        
        btnManualEntry.setOnClickListener(v -> {
            // Go back to timetable activity for manual entry
            finish();
        });
    }
    
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            return ContextCompat.checkSelfPermission(this, 
                android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0+
            return ContextCompat.checkSelfPermission(this, 
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
    
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - Request new media permissions
            if (ContextCompat.checkSelfPermission(this, 
                    android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO
                    }, REQUEST_PERMISSION);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 to 12 - Request legacy storage permission
            if (ContextCompat.checkSelfPermission(this, 
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 
                    REQUEST_PERMISSION);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✅ Permission granted! You can now upload files.", Toast.LENGTH_SHORT).show();
                tvStatus.setText("✅ Ready to upload files");
                tvStatus.setTextColor(0xFF10B981);
            } else {
                Toast.makeText(this, "❌ Permission denied. Cannot access files.", Toast.LENGTH_LONG).show();
                tvStatus.setText("❌ Permission required to access files");
                tvStatus.setTextColor(0xFFEF4444);
            }
        }
    }
    
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    
    private void pickPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_PDF_PICK);
    }
    
    private void pickExcel() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_EXCEL_PICK);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    processImage(selectedFileUri);
                    break;
                case REQUEST_PDF_PICK:
                    processPDF(selectedFileUri);
                    break;
                case REQUEST_EXCEL_PICK:
                    processExcel(selectedFileUri);
                    break;
            }
        }
    }
    
    private void processImage(Uri imageUri) {
        try {
            ivPreview.setImageURI(imageUri);
            previewLayout.setVisibility(android.view.View.VISIBLE);
            tvStatus.setText("📤 Processing image with AI...");
            tvStatus.setTextColor(0xFF3B82F6);
            
            // Get file path from URI
            String filePath = getFilePathFromUri(imageUri);
            if (filePath == null) {
                tvStatus.setText("❌ Could not access image file");
                tvStatus.setTextColor(0xFFEF4444);
                return;
            }
            
            java.io.File imageFile = new java.io.File(filePath);
            
            // Upload to Gemini API for OCR
            ApiService.uploadTimetableImage(this, imageFile, new ApiService.TimetableUploadCallback() {
                @Override
                public void onSuccess(java.util.List<java.util.Map<String, Object>> timetableEntries) {
                    runOnUiThread(() -> {
                        tvStatus.setText("✅ Timetable extracted and saved!");
                        tvStatus.setTextColor(0xFF10B981);
                        
                        new android.app.AlertDialog.Builder(TimetableUploadActivity.this)
                            .setTitle("🎉 Success!")
                            .setMessage("Your timetable has been extracted and saved!\n\n" +
                                "Found " + timetableEntries.size() + " lectures.\n\n" +
                                "You will receive notifications 10 minutes before each lecture.")
                            .setPositiveButton("View Timetable", (dialog, which) -> {
                                // Schedule notifications for all lectures
                                TimetableNotificationHelper.scheduleAllNotifications(TimetableUploadActivity.this);
                                finish();
                            })
                            .setNegativeButton("OK", (dialog, which) -> {
                                // Schedule notifications
                                TimetableNotificationHelper.scheduleAllNotifications(TimetableUploadActivity.this);
                                finish();
                            })
                            .show();
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        tvStatus.setText("❌ Error: " + error);
                        tvStatus.setTextColor(0xFFEF4444);
                        Toast.makeText(TimetableUploadActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            tvStatus.setText("❌ Error loading image");
            tvStatus.setTextColor(0xFFEF4444);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getFilePathFromUri(Uri uri) {
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }
    
    private void processPDF(Uri pdfUri) {
        try {
            previewLayout.setVisibility(android.view.View.GONE);
            String fileName = getFileName(pdfUri);
            tvStatus.setText("✅ PDF loaded: " + fileName);
            tvStatus.setTextColor(0xFF10B981);
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("📄 PDF Loaded")
                .setMessage("PDF file upload successful!\n\n" +
                    "File: " + fileName + "\n\n" +
                    "⚠️ Automatic PDF parsing is coming soon.\n\n" +
                    "For now, please:\n" +
                    "1. Go back to Timetable\n" +
                    "2. Use 'Add Class' button\n" +
                    "3. Manually enter your schedule")
                .setPositiveButton("Go to Manual Entry", (dialog, which) -> finish())
                .setNegativeButton("OK", null)
                .show();
            
        } catch (Exception e) {
            tvStatus.setText("❌ Error loading PDF");
            tvStatus.setTextColor(0xFFEF4444);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void processExcel(Uri excelUri) {
        try {
            previewLayout.setVisibility(android.view.View.GONE);
            String fileName = getFileName(excelUri);
            tvStatus.setText("✅ Excel loaded: " + fileName);
            tvStatus.setTextColor(0xFF10B981);
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("📊 Excel Loaded")
                .setMessage("Excel file upload successful!\n\n" +
                    "File: " + fileName + "\n\n" +
                    "⚠️ Automatic Excel parsing is coming soon.\n\n" +
                    "For now, please:\n" +
                    "1. Go back to Timetable\n" +
                    "2. Use 'Add Class' button\n" +
                    "3. Manually enter your schedule")
                .setPositiveButton("Go to Manual Entry", (dialog, which) -> finish())
                .setNegativeButton("OK", null)
                .show();
            
        } catch (Exception e) {
            tvStatus.setText("❌ Error loading Excel file");
            tvStatus.setTextColor(0xFFEF4444);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getFileName(Uri uri) {
        String fileName = "Unknown";
        android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }
}
