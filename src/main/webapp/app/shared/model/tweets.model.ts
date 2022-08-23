import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';

export interface ITweets {
  id?: number;
  content?: string;
  createdAt?: string | null;
  updatedAt?: string | null;
  customer?: ICustomer | null;
}

export const defaultValue: Readonly<ITweets> = {};
