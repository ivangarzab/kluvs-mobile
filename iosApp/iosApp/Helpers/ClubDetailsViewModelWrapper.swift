//
//  ClubDetailsViewModelWrapper.swift
//  iosApp
//
//  Created by Ivan Garza Bermea on 12/4/25.
//
import Swift
import Shared

@MainActor
class ClubDetailsViewModelWrapper: ObservableObject {
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
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
                self?.isLoading = state.isLoading
                self?.error = state.error
                self?.clubDetails = state.currentClubDetails
                self?.activeSession = state.activeSession
                self?.members = state.members
            }
        }
        cancellables.append(stateCancellable)
    }

    func loadClubData(clubId: String) {
        helper.loadClubData(clubId: clubId)
    }

    func refresh() {
        helper.refresh()
    }

    deinit {
        cancellables.forEach { $0.close() }
    }
}
