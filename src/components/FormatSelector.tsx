import { Check, ChevronDown } from "lucide-react";
import * as Select from "@radix-ui/react-select";
import { cn } from "@/lib/utils";

const formats = [
  { value: "pdf", label: "PDF" },
  { value: "docx", label: "Word (DOCX)" },
  { value: "jpg", label: "JPEG Image" },
  { value: "png", label: "PNG Image" },
];

interface FormatSelectorProps {
  value: string;
  onChange: (value: string) => void;
  className?: string;
}

export const FormatSelector = ({
  value,
  onChange,
  className,
}: FormatSelectorProps) => {
  return (
    <Select.Root value={value} onValueChange={onChange}>
      <Select.Trigger
        className={cn(
          "flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50",
          "glass hover-lift",
          className
        )}
      >
        <Select.Value placeholder="Select format" />
        <Select.Icon>
          <ChevronDown className="h-4 w-4 opacity-50" />
        </Select.Icon>
      </Select.Trigger>
      <Select.Portal>
        <Select.Content className="glass overflow-hidden rounded-md border shadow-md animate-in fade-in-80">
          <Select.Viewport className="p-1">
            {formats.map((format) => (
              <Select.Item
                key={format.value}
                value={format.value}
                className={cn(
                  "relative flex cursor-default select-none items-center rounded-sm py-1.5 pl-8 pr-2 text-sm outline-none focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50",
                  "hover:bg-muted/50"
                )}
              >
                <span className="absolute left-2 flex h-3.5 w-3.5 items-center justify-center">
                  <Select.ItemIndicator>
                    <Check className="h-4 w-4" />
                  </Select.ItemIndicator>
                </span>
                <Select.ItemText>{format.label}</Select.ItemText>
              </Select.Item>
            ))}
          </Select.Viewport>
        </Select.Content>
      </Select.Portal>
    </Select.Root>
  );
};