import api from './api'

// 회원가입
export const join = (data) => api.post(`/user`, data)

// 로그인
export const login = (id, password) => { 
                      return api.get(`/login?id=${id}&password=${password}`) 
                    }
// 회원 정보
export const info = () => api.get(`/user/info`)

// 회원 정보 수정
export const update = (data) => api.put(`/user`, data)

// 회원 탈퇴
export const remove = (no) => api.delete(`/user/${no}`)

// 아이디 중복검사
export const checking = (id) => api.get(`/check/${id}`)