import dayjs from 'dayjs';
import { ITweets } from 'app/shared/model/tweets.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICustomer {
  id?: number;
  slug?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  tweets?: ITweets[] | null;
  user?: IUser | null;
  followers?: ICustomer[] | null;
  followeds?: ICustomer[] | null;
}

export const defaultValue: Readonly<ICustomer> = {};
