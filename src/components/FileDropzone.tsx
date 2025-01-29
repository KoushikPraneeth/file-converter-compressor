import { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { Upload, FileText, X } from "lucide-react";
import { cn } from "@/lib/utils";

interface FileDropzoneProps {
  onFileSelect: (file: File) => void;
  className?: string;
}

export const FileDropzone = ({ onFileSelect, className }: FileDropzoneProps) => {
  const [isDragging, setIsDragging] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const onDrop = useCallback(
    (acceptedFiles: File[]) => {
      const file = acceptedFiles[0];
      setSelectedFile(file);
      onFileSelect(file);
    },
    [onFileSelect]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: false,
  });

  const removeFile = () => {
    setSelectedFile(null);
  };

  return (
    <div
      className={cn(
        "w-full p-8 transition-all duration-300",
        className
      )}
    >
      <div
        {...getRootProps()}
        className={cn(
          "relative w-full rounded-lg border-2 border-dashed p-8 transition-all duration-300",
          isDragActive
            ? "border-primary bg-primary/5"
            : "border-muted-foreground/25",
          selectedFile ? "bg-muted/50" : "hover:bg-muted/50",
          "glass hover-lift cursor-pointer"
        )}
      >
        <input {...getInputProps()} />
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          {selectedFile ? (
            <>
              <div className="flex items-center space-x-4">
                <FileText className="h-8 w-8 text-primary" />
                <span className="text-lg font-medium">{selectedFile.name}</span>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    removeFile();
                  }}
                  className="rounded-full p-1 hover:bg-muted"
                >
                  <X className="h-5 w-5 text-muted-foreground" />
                </button>
              </div>
              <p className="text-sm text-muted-foreground">
                Click or drag to replace
              </p>
            </>
          ) : (
            <>
              <Upload className="h-12 w-12 text-muted-foreground animate-float" />
              <div>
                <p className="text-lg font-medium">
                  Drop your file here, or{" "}
                  <span className="text-primary">browse</span>
                </p>
                <p className="text-sm text-muted-foreground">
                  Supports PDF, DOCX, JPG, PNG and more
                </p>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};