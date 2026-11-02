package com.example.atividade_2bim_2sem

import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomScannerActivity : CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_custom_scanner)
        return findViewById(R.id.zxing_barcode_scanner)
    }
}
