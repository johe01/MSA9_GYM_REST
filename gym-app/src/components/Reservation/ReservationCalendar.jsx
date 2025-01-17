import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import FullCalendar from '@fullcalendar/react'
import timeGridPlugin from '@fullcalendar/timegrid'
import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Reservation.css'

const ReservationCalendar = ({ reservationList, handleFilterChange, keyword, code }) => {

  const navigate = useNavigate()

  const formattedEvents = reservationList.map((event) => ({
    title: event.title,
    start: event.start,
    end: event.end,
    description: event.description,
    color: event.color,
    textColor: event.textColor,
    type: event.type,
    user_no: event.user_no,
    display: "block"
  }))


  return (
    <div className="Reservation-calendar">
      <div className="select-trainer">
        <select id="trainerList" name='keyword' value={keyword} onChange={(e) => handleFilterChange('keyword', e.target.value)}>
          <option value="">전체</option>
          {reservationList.map((trainer) => (
            <option key={trainer.no} value={trainer.no}>{trainer.name}</option>
          ))}
        </select>
      </div>
      <div className="select-status">
        <select name="code" value={code} onChange={(e) => handleFilterChange('code, e.target.value')}>
          <option value="">전체</option>
          <option value="1">예약중</option>
          <option value="2">완료건</option>
        </select>
      </div>

      <div className="calendar-wrap">
        <FullCalendar
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          initialView="dayGridMonth"
          height="700px"
          events={formattedEvents}
          displayEventTime={false}
          dayMaxEventRows={3}
          eventDidMount={(info) => {
            const eventEl = info.el;
            eventEl.style.cursor = 'pointer';

            const originalColor = info.event.backgroundColor;
            const hoverColor =
              info.event.extendedProps.type === 'completed'
                ? 'green'
                : 'royalblue';

            eventEl.addEventListener('mouseover', () => {
              eventEl.style.backgroundColor = hoverColor;
            });

            eventEl.addEventListener('mouseout', () => {
              eventEl.style.backgroundColor = originalColor;
            });

            const count = info.event.title.split(' ')[0];
            const date = info.event.startStr.slice(0, 10);
            const dateCell = document.querySelector(`.fc-day[data-date="${date}"]`);

            if (dateCell) {
              const countByDateDiv = document.createElement('div');
              countByDateDiv.classList.add('reservation-count');
              countByDateDiv.textContent = count;
              dateCell.appendChild(countByDateDiv);
            }
          }}
          dateClick={(info) => {
            showTimeSelectionModal(info.dateStr);
          }}
          eventClick={(info) => {
            const userNo = info.event.extendedProps.user_no;
            navigate(`/plan/plan?userNo=${userNo}`);
          }}
        />
      </div>

    </div>
  )
}

export default ReservationCalendar
