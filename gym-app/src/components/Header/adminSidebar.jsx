import React from 'react';

const Sidebar = () => {
  return (
    <div className="sidebar">
        <div className="sidebar-header">
            <a href="/admin/reservation/list">
                <h2>관리</h2>
            </a>
        </div>
        <nav>
            <ul className="main-menu">
                <li>
                    <h3>회원</h3>
                    <ul className="sub-menu">
                        <li><a href="/admin/user/list">회원 목록</a></li>
                        <li><a href="/admin/user/insert">회원 등록</a></li>
                    </ul>
                </li>
                <li>
                    <h3>트레이너</h3>
                    <ul className="sub-menu">
                        <li><a href="/admin/trainer/list">트레이너 목록</a></li>
                        <li><a href="/admin/trainer/insert">트레이너 등록</a></li>
                    </ul>
                </li>
                <li>
                    <h3>출석</h3>
                    <ul className="sub-menu">
                        <li><a href="/admin/attendance/list">출석 회원 목록</a></li>
                    </ul>
                </li>
                <li>
                    <h3>이용권</h3>
                    <ul className="sub-menu">
                        <li><a href="/admin/ticket/list">이용권 목록</a></li>
                        <li><a href="/admin/ticket/insert">이용권 등록</a></li>
                    </ul>
                </li>
                <li>
                    <h3>예약</h3>
                    <ul className="sub-menu">
                        <li><a href="/admin/reservation/list">예약 목록</a></li>
                        <li><a href="/admin/reservation/calendar">월별 예약 일정</a></li>
                    </ul>
                </li>
                <li>
                    <h3>매출</h3>
                    <ul className="sub-menu">
                        <li><a href="/admin/sales/salesList">매출 조회</a></li>
                        <li><a href="/admin/sales/buyList">구매내역 조회</a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>
  );
};

export default Sidebar;
