import { useState } from "react";
import { FileDropzone } from "@/components/FileDropzone";
import { FormatSelector } from "@/components/FormatSelector";
import { Download } from "lucide-react";

const Index = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [targetFormat, setTargetFormat] = useState("pdf");

  const handleConvert = () => {
    // Implement conversion logic
    console.log("Converting", selectedFile?.name, "to", targetFormat);
  };

  return (
    <div className="min-h-screen w-full p-4 md:p-8 space-y-8 animate-fadeIn">
      <header className="text-center space-y-4">
        <h1 className="text-4xl font-bold tracking-tight">File Converter</h1>
        <p className="text-lg text-muted-foreground">
          Convert your files to any format with ease
        </p>
      </header>

      <main className="max-w-3xl mx-auto space-y-8">
        <div className="glass rounded-xl p-6 space-y-6">
          <FileDropzone
            onFileSelect={setSelectedFile}
            className="animate-fadeIn"
          />

          <div className="space-y-4 animate-fadeIn">
            <label className="text-sm font-medium">Convert to:</label>
            <FormatSelector
              value={targetFormat}
              onChange={setTargetFormat}
              className="w-full"
            />
          </div>

          <button
            onClick={handleConvert}
            disabled={!selectedFile}
            className={cn(
              "w-full py-3 px-4 rounded-lg font-medium flex items-center justify-center space-x-2",
              "glass hover-lift transition-all duration-300",
              "disabled:opacity-50 disabled:cursor-not-allowed",
              selectedFile
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground"
            )}
          >
            <Download className="w-5 h-5" />
            <span>Convert Now</span>
          </button>
        </div>

        <section className="space-y-4">
          <h2 className="text-xl font-semibold">Recent Conversions</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Recent files will go here */}
          </div>
        </section>
      </main>
    </div>
  );
};

export default Index;