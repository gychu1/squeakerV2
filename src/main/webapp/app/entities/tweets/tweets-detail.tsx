import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tweets.reducer';

export const TweetsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const tweetsEntity = useAppSelector(state => state.tweets.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tweetsDetailsHeading">Tweets</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{tweetsEntity.id}</dd>
          <dt>
            <span id="content">Content</span>
          </dt>
          <dd>{tweetsEntity.content}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>{tweetsEntity.createdAt ? <TextFormat value={tweetsEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>{tweetsEntity.updatedAt ? <TextFormat value={tweetsEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Customer</dt>
          <dd>{tweetsEntity.customer ? tweetsEntity.customer.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/tweets" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tweets/${tweetsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default TweetsDetail;
