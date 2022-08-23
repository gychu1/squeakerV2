import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tweets from './tweets';
import TweetsDetail from './tweets-detail';
import TweetsUpdate from './tweets-update';
import TweetsDeleteDialog from './tweets-delete-dialog';

const TweetsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Tweets />} />
    <Route path="new" element={<TweetsUpdate />} />
    <Route path=":id">
      <Route index element={<TweetsDetail />} />
      <Route path="edit" element={<TweetsUpdate />} />
      <Route path="delete" element={<TweetsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TweetsRoutes;
