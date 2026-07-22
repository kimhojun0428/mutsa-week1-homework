const API_BASE_URL = "https://mutsa.dev.me.kr";
const TOKEN_STORAGE_KEY = "mutsa_oauth_access_token";

const elements = {
  notice: document.querySelector("#notice"),
  emptyState: document.querySelector("#empty-state"),
  profileState: document.querySelector("#profile-state"),
  loginButton: document.querySelector("#login-button"),
  reloadButton: document.querySelector("#reload-button"),
  logoutButton: document.querySelector("#logout-button"),
  profileImage: document.querySelector("#profile-image"),
  avatarFallback: document.querySelector("#avatar-fallback"),
  profileName: document.querySelector("#profile-name"),
  profileEmail: document.querySelector("#profile-email"),
  profileId: document.querySelector("#profile-id"),
  profileProvider: document.querySelector("#profile-provider"),
  profileCredit: document.querySelector("#profile-credit"),
  responseJson: document.querySelector("#response-json"),
};

elements.loginButton.addEventListener("click", () => {
  window.location.href = `${API_BASE_URL}/oauth2/authorization/kakao`;
});

elements.reloadButton.addEventListener("click", loadMyProfile);
elements.logoutButton.addEventListener("click", logout);

async function initialize() {
  const query = new URLSearchParams(window.location.search);
  const accessToken = query.get("accessToken");
  const error = query.get("error");

  if (accessToken) {
    localStorage.setItem(TOKEN_STORAGE_KEY, accessToken);
    history.replaceState({}, document.title, "/");
    setNotice("카카오 인증이 완료됐습니다. 회원 정보를 불러오는 중입니다.", "success");
  } else if (error) {
    history.replaceState({}, document.title, "/");
    setNotice(`카카오 인증에 실패했습니다: ${error}`, "error");
    return;
  }

  if (getAccessToken()) {
    await loadMyProfile();
  }
}

async function loadMyProfile() {
  const accessToken = getAccessToken();
  if (!accessToken) {
    showLoggedOut();
    return;
  }

  setButtonsDisabled(true);
  setNotice("백엔드에서 로그인한 회원 정보를 불러오는 중입니다.");

  try {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    const result = await readJson(response);

    if (!response.ok || !result.success) {
      throw new Error(result.message ?? `회원 정보 조회 실패 (${response.status})`);
    }

    renderProfile(result.data, result);
    setNotice("로그인 또는 자동 회원가입이 정상적으로 완료됐습니다.", "success");
  } catch (error) {
    if (String(error.message).includes("401")) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
    }
    showLoggedOut();
    setNotice(`회원 정보를 불러오지 못했습니다: ${error.message}`, "error");
  } finally {
    setButtonsDisabled(false);
  }
}

async function logout() {
  const accessToken = getAccessToken();
  setButtonsDisabled(true);

  try {
    if (accessToken) {
      await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
    }
  } finally {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    showLoggedOut();
    setNotice("브라우저에 저장된 Access Token을 삭제했습니다.");
    setButtonsDisabled(false);
  }
}

function renderProfile(profile, fullResponse) {
  elements.emptyState.hidden = true;
  elements.profileState.hidden = false;
  elements.profileName.textContent = profile.name ?? "이름 없음";
  elements.profileEmail.textContent = profile.email ?? "이메일 없음";
  elements.profileId.textContent = profile.id ?? "-";
  elements.profileProvider.textContent = profile.provider ?? "-";
  elements.profileCredit.textContent = Number(profile.credit ?? 0).toLocaleString("ko-KR");
  elements.responseJson.textContent = JSON.stringify(fullResponse, null, 2);
  elements.avatarFallback.textContent = (profile.name ?? "U").trim().charAt(0).toUpperCase() || "U";

  if (profile.profileImageUrl) {
    elements.profileImage.src = profile.profileImageUrl;
    elements.profileImage.hidden = false;
    elements.avatarFallback.hidden = true;
  } else {
    elements.profileImage.removeAttribute("src");
    elements.profileImage.hidden = true;
    elements.avatarFallback.hidden = false;
  }
}

function showLoggedOut() {
  elements.emptyState.hidden = false;
  elements.profileState.hidden = true;
}

function setNotice(message, tone = "neutral") {
  elements.notice.textContent = message;
  elements.notice.dataset.tone = tone;
}

function setButtonsDisabled(disabled) {
  elements.loginButton.disabled = disabled;
  elements.reloadButton.disabled = disabled;
  elements.logoutButton.disabled = disabled;
}

function getAccessToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

async function readJson(response) {
  try {
    return await response.json();
  } catch {
    throw new Error(`JSON이 아닌 응답을 받았습니다 (${response.status})`);
  }
}

initialize();
