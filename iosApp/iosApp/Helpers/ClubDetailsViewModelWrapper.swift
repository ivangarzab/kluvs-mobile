//
//  ClubDetailsViewModelWrapper.swift
//  iosApp
//
//  Created by Ivan Garza Bermea on 12/4/25.
//
import Swift
import Shared

enum ClubScreenState {
    case loading
    case error(String)
    case empty
    case content
}

@MainActor
class ClubDetailsViewModelWrapper: ObservableObject {
    @Published var screenState: ClubScreenState = .loading
    @Published var isLoading: Bool = false
    @Published var availableClubs: [Shared.ClubListItem] = []
    @Published var selectedClubId: String? = nil
    @Published var clubDetails: Shared.ClubDetails? = nil
    @Published var activeSession: Shared.ActiveSessionDetails? = nil
    @Published var members: [Shared.MemberListItemInfo] = []

    private let helper: ClubDetailsViewModelHelper
    private var cancellables: [Shared.Closeable] = []

    init() {
        self.helper = ClubDetailsViewModelHelper()
        startObserving()
    }

    private func startObserving() {
        let stateCancellable = helper.observeState { [weak self] state in
            DispatchQueue.main.async {
                self?.screenState = {
                    if !state.availableClubs.isEmpty { return .content }
                    if state.isLoading { return .loading }
                    if let error = state.error { return .error(error) }
                    return .empty
                }()
                self?.isLoading = state.isLoading
                self?.availableClubs = state.availableClubs
                self?.selectedClubId = state.selectedClubId
                self?.clubDetails = state.currentClubDetails
                self?.activeSession = state.activeSession
                self?.members = state.members
            }
        }
        cancellables.append(stateCancellable)
    }

    func loadUserClubs(userId: String) {
        helper.loadUserClubs(userId: userId)
    }

    func loadClubData(clubId: String) {
        helper.loadClubData(clubId: clubId)
    }

    func selectClub(clubId: String) {
        helper.selectClub(clubId: clubId)
    }

    func refresh() {
        helper.refresh()
    }

    deinit {
        cancellables.forEach { $0.close() }
    }
}
