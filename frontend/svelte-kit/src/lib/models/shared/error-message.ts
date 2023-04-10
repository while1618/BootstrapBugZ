export interface ErrorMessage {
  timestamp: Date;
  status: number;
  error: string;
  details: [
    {
      field?: string;
      message: string;
    }
  ];
}
